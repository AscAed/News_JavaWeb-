## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2026-07-24 - Interactive Custom Cards Keyboard Accessibility
**Learning:** Found custom interactive components (like `NewsCard`) that act as links/buttons but lack native focusability and keyboard event handlers. They were purely reliant on `@click` events, rendering them inaccessible to keyboard users.
**Action:** When creating custom interactive blocks that act as clickable elements, ensure to add `tabindex="0"`, `role="button"` or `role="article"`, an `aria-label`, keyboard event handlers for `@keydown.enter` and `@keydown.space`, and `:focus-visible` styling.
