## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2025-07-14 - Keyboard Accessibility for Clickable Divs
**Learning:** Found that custom clickable components (like `div.news-card`) using `@click` were inaccessible to keyboard users because they lacked `tabindex`, `role`, ARIA labels, keyboard event listeners (`@keydown.enter`, `@keydown.space`), and focus visible outlines.
**Action:** When creating custom interactive elements out of non-semantic HTML tags, ensure they have full keyboard navigation support and focus states that match hover behavior.
