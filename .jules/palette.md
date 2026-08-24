## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2025-07-13 - Add ARIA labels to Element Plus form inputs missing explicit labels
**Learning:** Discovered that in Element Plus forms where `el-form-item` relies solely on placeholders instead of the `label` prop, screen readers lack an accessible name for the `el-input` components. Additionally, icon-only buttons inside input templates (like `#append`) also lack accessible names.
**Action:** Always add an explicit `aria-label` attribute to `el-input` components when they don't have a visible label text, and to icon-only `el-button`s inside input appendages to ensure proper screen reader accessibility.
