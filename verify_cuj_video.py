from playwright.sync_api import sync_playwright

def run_cuj(page):
    # Verify Home (AppHeader search input)
    page.goto('http://localhost:5173/')
    page.wait_for_timeout(500)
    search_input = page.locator('input[aria-label="搜索新闻"]')
    search_input.wait_for(state='visible')
    if search_input.count() > 0:
        print("Successfully found '搜索新闻' aria-label in Home")
    page.wait_for_timeout(500)

    # Verify Login
    page.goto('http://localhost:5173/login')
    page.wait_for_timeout(500)
    phone_input = page.locator('input[aria-label="手机号"]')
    password_input = page.locator('input[aria-label="密码"]')
    phone_input.wait_for(state='visible')
    password_input.wait_for(state='visible')
    if phone_input.count() > 0 and password_input.count() > 0:
         print("Successfully found '手机号' and '密码' aria-labels in Login")
    page.wait_for_timeout(500)

    # Verify Register
    page.goto('http://localhost:5173/register')
    page.wait_for_timeout(500)
    username_input = page.locator('input[aria-label="用户名"]')
    email_input = page.locator('input[aria-label="邮箱"]')
    username_input.wait_for(state='visible')
    email_input.wait_for(state='visible')
    if username_input.count() > 0 and email_input.count() > 0:
        print("Successfully found '用户名' and '邮箱' aria-labels in Register")

    page.screenshot(path='/home/jules/verification/screenshots/verification.png')
    page.wait_for_timeout(1000)

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
            context.close()
            browser.close()
