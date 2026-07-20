from playwright.sync_api import sync_playwright
import time

def verify_news_detail():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()
        try:
            print("Mocking backend APIs...")

            # Mock headline detail API
            page.route("**/api/v1/headlines/123", lambda route: route.fulfill(
                status=200,
                content_type="application/json",
                body='{"code": 200, "data": {"hid": 123, "title": "Test News Detail", "content": "<div><h2>Hello World</h2><p>This is a mock news content to test DOMPurify sanitize.</p></div>", "author": "Test Author", "publishedTime": "2023-10-27T10:00:00Z", "typeName": "Technology", "pageViews": 100, "likeCount": 10, "commentCount": 5, "tags": "mock, test, vue"}, "message": "success"}'
            ))

            # Mock comments API
            page.route("**/api/v1/comments*", lambda route: route.fulfill(
                status=200,
                content_type="application/json",
                body='{"code": 200, "data": {"items": [{"id": "c1", "content": "Great article!", "author": {"username": "User1"}, "created_time": "2023-10-27T11:00:00Z", "likeCount": 2, "replies": []}], "total": 1}, "message": "success"}'
            ))

            print("Navigating directly to news detail...")
            page.goto("http://localhost:5173/news/123")
            time.sleep(2) # wait for page to load

            print("Taking screenshot of news detail...")
            page.screenshot(path="verify_detail_render.png", full_page=True)
            print("Done")
        except Exception as e:
            print(f"Error: {e}")
        finally:
            browser.close()

if __name__ == "__main__":
    verify_news_detail()
