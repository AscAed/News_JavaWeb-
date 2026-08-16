## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2025-07-13 - Adding ARIA labels to el-input fields in forms
**Learning:** Element Plus `el-form-item` components don't always use the `label` prop, especially in designs that rely on placeholders for inputs (e.g., Login/Register pages). This lack of associated labels creates a poor experience for screen reader users.
**Action:** Always verify that `el-input` fields have explicit `aria-label` attributes if the parent `el-form-item` does not explicitly provide an accessible text label.
