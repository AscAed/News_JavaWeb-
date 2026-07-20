from playwright.sync_api import sync_playwright
import time

def verify_news_detail():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()
        try:
            print("Mocking backend APIs...")
            # Mock categories API
            page.route("**/api/v1/categories*", lambda route: route.fulfill(
                status=200,
                content_type="application/json",
                body='{"code": 200, "data": [{"id": 1, "name": "Technology"}], "message": "success"}'
            ))

            # Mock headlines list API
            page.route("**/api/v1/headlines*", lambda route: route.fulfill(
                status=200,
                content_type="application/json",
                body='{"code": 200, "data": {"records": [{"hid": 123, "title": "Test News", "summary": "Test Summary", "author": "Test Author", "publishedTime": "2023-10-27T10:00:00Z", "typeName": "Technology", "pageViews": 100, "likeCount": 10, "commentCount": 5}], "total": 1, "size": 10, "current": 1, "pages": 1}, "message": "success"}'
            ))

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

            # Mock subscriptions API
            page.route("**/api/v1/rss-subscriptions/list*", lambda route: route.fulfill(
                status=200,
                content_type="application/json",
                body='{"code": 200, "data": [], "message": "success"}'
            ))

            print("Navigating to frontend...")
            page.goto("http://localhost:5173")
            time.sleep(2) # wait for page to load

            print("Clicking first news card...")
            page.locator('.news-card').first.click()

            time.sleep(2) # wait for details to load

            print("Entering text to test typing lag...")
            page.fill('textarea[placeholder="发表你的评论..."]', 'Typing a comment to test performance')
            time.sleep(1)

            print("Taking screenshot of news detail...")
            page.screenshot(path="verify_detail_render.png", full_page=True)
            print("Done")
        except Exception as e:
            print(f"Error: {e}")
        finally:
            browser.close()

if __name__ == "__main__":
    verify_news_detail()
