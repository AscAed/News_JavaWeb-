from playwright.sync_api import sync_playwright

def run_cuj(page):
    # Navigate to the news details page (assume hid 1 is available)
    page.goto("http://localhost:5173/news/1")
    page.wait_for_timeout(2000)

    # Scroll down slightly to make sure content rendered properly
    page.evaluate("window.scrollBy(0, 500)")
    page.wait_for_timeout(1000)

    # Try typing into the comment box if it exists (might need login, but we can verify it doesn't crash)
    # Actually, let's just make sure the page loads fully. The optimization shouldn't change visuals.
    page.evaluate("window.scrollTo(0, document.body.scrollHeight)")
    page.wait_for_timeout(1000)

    # Scroll back up to the content
    page.evaluate("window.scrollTo(0, 0)")
    page.wait_for_timeout(1000)

    # Take screenshot at the key moment
    page.screenshot(path="/home/jules/verification/screenshots/verification.png")
    page.wait_for_timeout(1000)  # Hold final state for the video

if __name__ == "__main__":
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(
            record_video_dir="/home/jules/verification/videos"
        )
        page = context.new_page()
        try:
            run_cuj(page)
        finally:
            context.close()  # MUST close context to save the video
            browser.close()