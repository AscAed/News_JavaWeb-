## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2025-02-13 - [Semantic Floating Action Buttons]
**Learning:** Custom interactive elements (like FABs) built with `<div>` lack built-in keyboard accessibility and semantic meaning for screen readers. Using a `<div>` for a button breaks keyboard navigation (tabbing) and prevents screen readers from announcing it as a clickable action.
**Action:** Always use semantic `<button>` tags for interactive clickable elements instead of `<div>`. For icon-only buttons, always ensure an `aria-label` is provided and add `:focus-visible` styles so keyboard users can see when the button is focused.
