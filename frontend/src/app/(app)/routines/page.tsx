//on desktop the detail pane next to the plans list, on phones the list fills the page alone
export default function PlansPage() {
  return (
    <div className="hidden flex-col items-center justify-center px-4 text-center lg:flex">
      <h2 className="text-title">No routine selected</h2>
      <p className="mt-1 text-text-secondary">Pick a routine to see its exercises.</p>
    </div>
  );
}
