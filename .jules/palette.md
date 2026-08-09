## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2025-07-13 - Adding ARIA labels to placeholder-only form fields
**Learning:** Element Plus `el-form-item` components commonly use placeholders instead of explicit `<label>` tags (especially in login/registration forms), which leaves input fields without accessible names for screen readers.
**Action:** Always ensure that `el-input` components that rely solely on placeholders for visual labeling have an explicit `aria-label` attribute added to them to support accessibility.
