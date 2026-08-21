## 2024-05-20 - [Debouncing Search without Dependencies]
**Learning:** Adding standard utility packages like `lodash` just for a single function like `debounce` can bloat the frontend bundle and violates the "do not add dependencies unless necessary" rule. Custom utility functions are often preferred for simple optimizations when external dependencies are constrained.
**Action:** When a utility like `debounce` or `throttle` is needed for a single component or specific use-case, write a native Typescript implementation within the component or a local `utils` file to maintain a zero-dependency footprint. Always ensure proper cleanup (e.g., calling `cancel()` in `onUnmounted`) to prevent memory leaks or state updates on destroyed components.

## 2024-05-23 - [Vue Template Function Calls in List Rendering]
**Learning:** Avoid using functions like `DOMPurify.sanitize()` or date formatters directly inside the Vue template bindings (e.g. `v-html="DOMPurify.sanitize(news.title)"` or within `v-for="tag in getTagList(news.tags)"`). In a list-rendering context like a news feed where a component might re-render, inline function calls evaluate on every render cycle which multiplies the performance cost and forces Vue to unnecessarily recalculate and recreate virtual DOM diffs.
**Action:** When working with list items in Vue (or React), memoize expensive formatting and data manipulation logic using `computed` properties (or `useMemo` in React). Ensure that properties like arrays returned by strings (`tagList`) and date parsing strings (`formattedTime`) are cached at the component instance level.

## 2024-05-24 - [Vue Template Function Calls in Detail Views]
**Learning:** Inline function calls in Vue templates (like `DOMPurify.sanitize()` or array parsing) inside detail views with interactive elements (like comment inputs) will re-evaluate on every keystroke, causing unnecessary virtual DOM diffs and potential main thread blocking.
**Action:** Always extract expensive string formatting, date parsing, and sanitization logic to `computed` properties in Vue components, especially when the view contains interactive state that triggers frequent re-renders.
