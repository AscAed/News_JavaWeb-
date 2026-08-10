## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2024-08-11 - Adding ARIA labels to custom UI elements and icon-buttons
**Learning:** Found instances where `div` elements act as buttons (e.g., floating action buttons or slider handles) and lack `aria-label`s. Also, some buttons with both icons and text (like "清空记录") benefit from explicit `aria-label`s if the text isn't semantically complete or screen-reader friendly.
**Action:** Always ensure that custom interactive elements (like `div`s with `@click` handlers acting as buttons) have appropriate `aria-label` and `role="button"` attributes. Review buttons with icons to ensure they provide a complete accessible name.
