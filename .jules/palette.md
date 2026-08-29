## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2026-08-29 - Adding ARIA labels to unlabelled Element Plus inputs
**Learning:** Found instances where `el-form-item` relies solely on the inner input's `placeholder` instead of providing a `label` prop. While visually identical, this leaves the input without a guaranteed accessible name for screen readers, as placeholders are not reliably announced as labels across all assistive technologies.
**Action:** Always ensure `el-input` components explicitly define an `aria-label` attribute when their wrapping `el-form-item` lacks a `label` prop.
