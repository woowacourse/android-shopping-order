#!/usr/bin/env python3
"""Simple stateful mock backend for android-shopping-order."""

from __future__ import annotations

import argparse
import json
import math
from dataclasses import dataclass
from http import HTTPStatus
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from threading import Lock
from typing import Any
from urllib.parse import parse_qs, urlparse


def make_product(product_id: int, category: str, name: str, price: int, image: str) -> dict[str, Any]:
    return {
        "id": product_id,
        "name": name,
        "price": price,
        "imageUrl": image,
        "category": category,
    }


SEED_PRODUCTS: list[dict[str, Any]] = [
    make_product(1, "snack", "Honey Butter Chips", 2200, "https://picsum.photos/id/237/400/400"),
    make_product(2, "snack", "Salted Pretzel", 1800, "https://picsum.photos/id/102/400/400"),
    make_product(3, "snack", "Chocolate Cookie", 2500, "https://picsum.photos/id/1060/400/400"),
    make_product(4, "snack", "Wasabi Almond", 4300, "https://picsum.photos/id/1080/400/400"),
    make_product(5, "snack", "Sweet Popcorn", 3300, "https://picsum.photos/id/292/400/400"),
    make_product(6, "snack", "Rice Cracker Mix", 2900, "https://picsum.photos/id/1084/400/400"),
    make_product(7, "snack", "Seaweed Chips", 2700, "https://picsum.photos/id/1082/400/400"),
    make_product(8, "snack", "Energy Bar", 1600, "https://picsum.photos/id/1081/400/400"),
    make_product(9, "drink", "Sparkling Water", 1200, "https://picsum.photos/id/1040/400/400"),
    make_product(10, "drink", "Cola Zero", 1900, "https://picsum.photos/id/1041/400/400"),
    make_product(11, "drink", "Orange Juice", 2400, "https://picsum.photos/id/1042/400/400"),
    make_product(12, "drink", "Cold Brew", 3200, "https://picsum.photos/id/1043/400/400"),
    make_product(13, "drink", "Green Tea", 2100, "https://picsum.photos/id/1044/400/400"),
    make_product(14, "drink", "Protein Shake", 3500, "https://picsum.photos/id/1045/400/400"),
    make_product(15, "drink", "Yogurt Drink", 1700, "https://picsum.photos/id/1047/400/400"),
    make_product(16, "drink", "Apple Ade", 2800, "https://picsum.photos/id/1048/400/400"),
    make_product(17, "fresh", "Banana Pack", 3900, "https://picsum.photos/id/1025/400/400"),
    make_product(18, "fresh", "Cherry Tomato", 4200, "https://picsum.photos/id/1059/400/400"),
    make_product(19, "fresh", "Greek Yogurt", 4600, "https://picsum.photos/id/100/400/400"),
    make_product(20, "fresh", "Avocado", 2100, "https://picsum.photos/id/101/400/400"),
    make_product(21, "fresh", "Mixed Salad", 3800, "https://picsum.photos/id/103/400/400"),
    make_product(22, "fresh", "Blueberry", 6900, "https://picsum.photos/id/107/400/400"),
    make_product(23, "fresh", "Chicken Breast", 5900, "https://picsum.photos/id/108/400/400"),
    make_product(24, "fresh", "Boiled Egg", 3200, "https://picsum.photos/id/110/400/400"),
    make_product(25, "home", "Dish Soap", 4900, "https://picsum.photos/id/111/400/400"),
    make_product(26, "home", "Paper Towels", 7200, "https://picsum.photos/id/112/400/400"),
    make_product(27, "home", "Laundry Pods", 10900, "https://picsum.photos/id/113/400/400"),
    make_product(28, "home", "Toothpaste", 3300, "https://picsum.photos/id/114/400/400"),
    make_product(29, "home", "Hand Wash", 3600, "https://picsum.photos/id/115/400/400"),
    make_product(30, "home", "Shampoo", 7900, "https://picsum.photos/id/116/400/400"),
    make_product(31, "frozen", "Frozen Dumplings", 8400, "https://picsum.photos/id/117/400/400"),
    make_product(32, "frozen", "Fish Cutlet", 7600, "https://picsum.photos/id/118/400/400"),
    make_product(33, "frozen", "Frozen Pizza", 9500, "https://picsum.photos/id/119/400/400"),
    make_product(34, "frozen", "French Fries", 5400, "https://picsum.photos/id/120/400/400"),
    make_product(35, "frozen", "Waffle", 4300, "https://picsum.photos/id/121/400/400"),
    make_product(36, "frozen", "Mandu Soup", 8800, "https://picsum.photos/id/122/400/400"),
    make_product(37, "bakery", "Whole Wheat Bread", 2900, "https://picsum.photos/id/123/400/400"),
    make_product(38, "bakery", "Butter Croissant", 2500, "https://picsum.photos/id/124/400/400"),
    make_product(39, "bakery", "Blueberry Muffin", 2800, "https://picsum.photos/id/125/400/400"),
    make_product(40, "bakery", "Cheese Bagel", 2600, "https://picsum.photos/id/126/400/400"),
]

SORT_META = {"empty": False, "sorted": True, "unsorted": False}


@dataclass
class CartItem:
    id: int
    product_id: int
    quantity: int


class Store:
    def __init__(self) -> None:
        self._lock = Lock()
        self.products: list[dict[str, Any]] = [dict(product) for product in SEED_PRODUCTS]
        self.cart_items: dict[int, CartItem] = {}
        self.next_product_id = max((product["id"] for product in self.products), default=0) + 1
        self.next_cart_item_id = 1

    def _find_product(self, product_id: int) -> dict[str, Any] | None:
        return next((product for product in self.products if product["id"] == product_id), None)

    def list_products(self, category: str | None, sort: list[str] | None) -> list[dict[str, Any]]:
        products = self.products
        if category:
            products = [product for product in products if product["category"] == category]

        sort_key = "id"
        descending = False
        if sort:
            raw_sort = sort[0]
            if "," in raw_sort:
                key, direction = raw_sort.split(",", 1)
                if key in {"id", "price", "name"}:
                    sort_key = key
                descending = direction.lower() == "desc"

        return sorted(products, key=lambda product: product.get(sort_key), reverse=descending)

    def paginate(self, items: list[dict[str, Any]], page: int, size: int) -> tuple[list[dict[str, Any]], int]:
        if size <= 0:
            size = 1
        if page < 0:
            page = 0
        start = page * size
        end = start + size
        total_pages = math.ceil(len(items) / size) if items else 0
        return items[start:end], total_pages

    def build_product_page(self, page: int, size: int, category: str | None, sort: list[str] | None) -> dict[str, Any]:
        products = self.list_products(category=category, sort=sort)
        content, total_pages = self.paginate(products, page, size)
        return {
            "totalElements": len(products),
            "totalPages": total_pages,
            "size": size,
            "content": content,
            "number": page,
            "sort": SORT_META,
            "pageable": {
                "offset": page * size,
                "sort": SORT_META,
                "paged": True,
                "pageNumber": page,
                "pageSize": size,
                "unpaged": False,
            },
            "first": page <= 0,
            "last": page >= max(total_pages - 1, 0),
            "numberOfElements": len(content),
            "empty": len(content) == 0,
        }

    def build_cart_page(self, page: int, size: int, sort: list[str] | None) -> dict[str, Any]:
        _ = sort
        cart_content = []
        for cart_item in self.cart_items.values():
            product = self._find_product(cart_item.product_id)
            if product is None:
                continue
            cart_content.append(
                {
                    "id": cart_item.id,
                    "product": product,
                    "quantity": cart_item.quantity,
                }
            )
        cart_content.sort(key=lambda item: item["id"])
        content, total_pages = self.paginate(cart_content, page, size)
        return {
            "content": content,
            "empty": len(content) == 0,
            "first": page <= 0,
            "last": page >= max(total_pages - 1, 0),
            "number": page,
            "numberOfElements": len(content),
            "pageable": {
                "offset": page * size,
                "pageNumber": page,
                "pageSize": size,
                "paged": True,
                "sort": SORT_META,
                "unpaged": False,
            },
            "size": size,
            "sort": SORT_META,
            "totalElements": len(cart_content),
            "totalPages": total_pages,
        }

    def add_product(self, payload: dict[str, Any]) -> dict[str, Any]:
        with self._lock:
            product_id = int(payload.get("id") or self.next_product_id)
            if payload.get("id") is None:
                self.next_product_id += 1

            product = {
                "id": product_id,
                "name": str(payload.get("name", "Unnamed Product")),
                "price": int(payload.get("price", 0)),
                "imageUrl": str(payload.get("imageUrl", "https://picsum.photos/seed/new-product/400/400")),
                "category": str(payload.get("category", "etc")),
            }

            existing = self._find_product(product_id)
            if existing is not None:
                self.products = [product if current["id"] == product_id else current for current in self.products]
            else:
                self.products.append(product)
            return product

    def delete_product(self, product_id: int) -> bool:
        with self._lock:
            original_length = len(self.products)
            self.products = [product for product in self.products if product["id"] != product_id]
            if len(self.products) == original_length:
                return False
            orphan_ids = [cart_id for cart_id, item in self.cart_items.items() if item.product_id == product_id]
            for cart_id in orphan_ids:
                del self.cart_items[cart_id]
            return True

    def get_product(self, product_id: int) -> dict[str, Any] | None:
        return self._find_product(product_id)

    def add_cart_item(self, payload: dict[str, Any]) -> CartItem:
        with self._lock:
            product_id = int(payload.get("productId"))
            quantity = int(payload.get("quantity", 1))
            if quantity <= 0:
                quantity = 1

            existing_item = next((item for item in self.cart_items.values() if item.product_id == product_id), None)
            if existing_item is not None:
                existing_item.quantity += quantity
                return existing_item

            cart_item = CartItem(id=self.next_cart_item_id, product_id=product_id, quantity=quantity)
            self.cart_items[cart_item.id] = cart_item
            self.next_cart_item_id += 1
            return cart_item

    def update_cart_item(self, cart_item_id: int, quantity: int) -> bool:
        with self._lock:
            item = self.cart_items.get(cart_item_id)
            if item is None:
                return False
            if quantity <= 0:
                del self.cart_items[cart_item_id]
            else:
                item.quantity = quantity
            return True

    def delete_cart_item(self, cart_item_id: int) -> bool:
        with self._lock:
            if cart_item_id not in self.cart_items:
                return False
            del self.cart_items[cart_item_id]
            return True

    def order(self, cart_item_ids: list[int]) -> None:
        with self._lock:
            for cart_item_id in cart_item_ids:
                self.cart_items.pop(cart_item_id, None)


STORE = Store()


class MockShoppingRequestHandler(BaseHTTPRequestHandler):
    server_version = "MockShoppingServer/1.0"

    def _read_json(self) -> dict[str, Any]:
        content_length = int(self.headers.get("Content-Length", 0))
        if content_length == 0:
            return {}
        payload = self.rfile.read(content_length).decode("utf-8")
        return json.loads(payload)

    def _send_json(self, status: int, body: Any) -> None:
        encoded = json.dumps(body).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(encoded)))
        self.end_headers()
        self.wfile.write(encoded)

    def _send_empty(self, status: int) -> None:
        self.send_response(status)
        self.send_header("Content-Length", "0")
        self.end_headers()

    def _not_found(self, message: str = "Not found") -> None:
        self._send_json(HTTPStatus.NOT_FOUND, {"message": message})

    def _bad_request(self, message: str) -> None:
        self._send_json(HTTPStatus.BAD_REQUEST, {"message": message})

    def do_GET(self) -> None:
        parsed = urlparse(self.path)
        path = parsed.path
        query = parse_qs(parsed.query)

        if path == "/health":
            self._send_json(HTTPStatus.OK, {"status": "ok"})
            return

        if path == "/products":
            page = int(query.get("page", [0])[0])
            size = int(query.get("size", [20])[0])
            sort = query.get("sort")
            category = query.get("category", [None])[0]
            response = STORE.build_product_page(page=page, size=size, category=category, sort=sort)
            self._send_json(HTTPStatus.OK, response)
            return

        if path.startswith("/products/"):
            try:
                product_id = int(path.split("/")[-1])
            except ValueError:
                self._bad_request("Invalid product id")
                return
            product = STORE.get_product(product_id)
            if product is None:
                self._not_found("Product not found")
                return
            self._send_json(HTTPStatus.OK, product)
            return

        if path == "/cart-items":
            page = int(query.get("page", [0])[0])
            size = int(query.get("size", [5])[0])
            sort = query.get("sort")
            response = STORE.build_cart_page(page=page, size=size, sort=sort)
            self._send_json(HTTPStatus.OK, response)
            return

        self._not_found()

    def do_POST(self) -> None:
        path = urlparse(self.path).path

        if path == "/products":
            payload = self._read_json()
            STORE.add_product(payload)
            self._send_empty(HTTPStatus.OK)
            return

        if path == "/cart-items":
            payload = self._read_json()
            product_id = int(payload.get("productId", 0))
            if STORE.get_product(product_id) is None:
                self._not_found("Product not found")
                return
            STORE.add_cart_item(payload)
            self._send_empty(HTTPStatus.OK)
            return

        if path == "/orders":
            payload = self._read_json()
            raw_ids = payload.get("cartItemIds", [])
            cart_item_ids = [int(cart_item_id) for cart_item_id in raw_ids]
            STORE.order(cart_item_ids)
            self._send_empty(HTTPStatus.OK)
            return

        self._not_found()

    def do_PATCH(self) -> None:
        path = urlparse(self.path).path
        if not path.startswith("/cart-items/"):
            self._not_found()
            return
        try:
            cart_item_id = int(path.split("/")[-1])
        except ValueError:
            self._bad_request("Invalid cart item id")
            return
        payload = self._read_json()
        quantity = int(payload.get("quantity", 0))
        updated = STORE.update_cart_item(cart_item_id=cart_item_id, quantity=quantity)
        if not updated:
            self._not_found("Cart item not found")
            return
        self._send_empty(HTTPStatus.OK)

    def do_DELETE(self) -> None:
        path = urlparse(self.path).path
        if path.startswith("/products/"):
            try:
                product_id = int(path.split("/")[-1])
            except ValueError:
                self._bad_request("Invalid product id")
                return
            deleted = STORE.delete_product(product_id)
            if not deleted:
                self._not_found("Product not found")
                return
            self._send_empty(HTTPStatus.OK)
            return

        if path.startswith("/cart-items/"):
            try:
                cart_item_id = int(path.split("/")[-1])
            except ValueError:
                self._bad_request("Invalid cart item id")
                return
            deleted = STORE.delete_cart_item(cart_item_id)
            if not deleted:
                self._not_found("Cart item not found")
                return
            self._send_empty(HTTPStatus.OK)
            return

        self._not_found()

    def log_message(self, fmt: str, *args: Any) -> None:
        print(f"[{self.log_date_time_string()}] {self.client_address[0]} {fmt % args}")


def main() -> None:
    parser = argparse.ArgumentParser(description="Run local mock backend for android-shopping-order")
    parser.add_argument("--host", default="0.0.0.0", help="Bind host")
    parser.add_argument("--port", type=int, default=8080, help="Bind port")
    args = parser.parse_args()

    server = ThreadingHTTPServer((args.host, args.port), MockShoppingRequestHandler)
    print(f"Mock shopping server running at http://{args.host}:{args.port}")
    print("Endpoints: /products, /products/{id}, /cart-items, /orders, /health")

    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nShutting down mock server...")
    finally:
        server.server_close()


if __name__ == "__main__":
    main()
