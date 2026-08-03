## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2026-08-03 - Adding ARIA labels to Element Plus inputs in placeholder-only forms
**Learning:** In Element Plus forms (e.g., Login/Register) where `el-form-item` relies on placeholders instead of the `label` prop, screen readers cannot implicitly determine the input's purpose.
**Action:** Always ensure explicit `aria-label` attributes are added to `el-input` components when the enclosing `el-form-item` omits the `label` prop.
