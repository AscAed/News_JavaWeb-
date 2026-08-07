## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2025-07-13 - Adding ARIA labels to el-input placeholders
**Learning:** In Element Plus forms (e.g., Login/Register) where `el-form-item` relies on placeholders instead of the `label` prop, the input fields lack accessible names, making them difficult for screen reader users to identify.
**Action:** Always ensure explicit `aria-label` attributes are added to `el-input` components when they are used without an explicit label or rely solely on placeholders for visual context.
