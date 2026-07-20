from playwright.sync_api import sync_playwright
import time

def verify_news_detail():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()
        try:
            print("Navigating to frontend...")
            page.goto("http://localhost:5173")
            time.sleep(2) # wait for page to load

            print("Clicking first news card...")
            page.locator('.news-card').first.click()

            time.sleep(2) # wait for details to load
            print("Taking screenshot of news detail...")
            page.screenshot(path="verify_detail_render.png", full_page=True)
            print("Done")
        except Exception as e:
            print(f"Error: {e}")
        finally:
            browser.close()

if __name__ == "__main__":
    verify_news_detail()
