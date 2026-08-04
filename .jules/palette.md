## 2025-07-11 - Adding ARIA labels to icon-only buttons
**Learning:** Found multiple instances of icon-only buttons (like search and theme toggles) lacking `aria-label` attributes. This is a crucial accessibility improvement for screen readers.
**Action:** Always check `el-button` components that only contain an `el-icon` (or `icon` prop) to ensure they have an appropriate `aria-label` if they lack text content.

## 2025-07-12 - Adding ARIA labels to buttons inside tooltips
**Learning:** Found an icon-only button wrapped inside an `el-tooltip` that lacked an `aria-label`. While the tooltip provides visual context on hover, it does not provide accessible names for screen readers natively in this implementation.
**Action:** Always ensure icon-only buttons have an `aria-label`, even if they are wrapped in a tooltip component.

## 2025-08-04 - Adding ARIA attributes to custom slider components
**Learning:** Found a custom slider captcha created using `div` elements that completely lacked accessibility attributes. Interactive custom UI elements (like sliders) require explicit roles and states to be usable by screen readers, as well as focus indicators for keyboard users.
**Action:** Always ensure custom interactive elements have appropriate ARIA roles (e.g., `role="slider"`), a `tabindex="0"` to be focusable, relevant ARIA state bindings (e.g., `aria-valuenow`, `aria-valuemin`, `aria-valuemax`), and explicit `:focus-visible` styling.
