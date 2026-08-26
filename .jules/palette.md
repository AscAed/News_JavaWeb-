## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2025-08-26 - Using Semantic Tags for Interactive Elements
**Learning:** Found interactive elements like a floating action button implemented using `<div>` with `@click` handlers. While this works for mouse users, it lacks native keyboard accessibility (focus state and Enter/Space activation) which is critical for a good UX.
**Action:** Always prefer semantic HTML elements like `<button>` for actions and `<a>` for navigation over `<div>` or `<span>` with click handlers. Ensure `aria-label` is provided if the button only contains an icon.
