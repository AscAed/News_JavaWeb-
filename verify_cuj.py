from playwright.sync_api import sync_playwright

def verify_frontend():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(record_video_dir="videos/")
        page = context.new_page()

        try:
            print("Navigating to home page...")
            page.goto("http://localhost:5174", timeout=10000)
            page.wait_for_load_state("networkidle")

            print("Navigating directly to news list...")
            page.goto("http://localhost:5174/news", timeout=10000)
            page.wait_for_load_state("networkidle")

            print("Taking screenshot of News List...")
            page.screenshot(path="news_list.png")

            print("Navigating directly to news detail...")
            page.goto("http://localhost:5174/news/1", timeout=10000)
            page.wait_for_load_state("networkidle")

            print("Taking screenshot of News Detail...")
            page.screenshot(path="news_detail.png", full_page=True)

            print("Testing comment input (re-render test)...")
            try:
                page.fill("textarea", "This is a test comment to trigger re-renders.")
                page.wait_for_timeout(1000)
                print("Taking screenshot of News Detail after typing...")
                page.screenshot(path="news_detail_typing.png")
            except Exception as inner_e:
                 print(f"Could not find textarea to test typing: {inner_e}")

            print("Verification successful!")

        except Exception as e:
            print(f"Error during verification: {e}")
            page.screenshot(path="error.png")
            raise e
        finally:
            context.close()
            browser.close()

if __name__ == "__main__":
    verify_frontend()
