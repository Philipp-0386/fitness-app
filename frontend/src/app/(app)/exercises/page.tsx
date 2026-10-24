//on desktop the detail pane next to the catalog, on phones the catalog fills the page alone
export default function ExercisesPage() {
  return (
    <div className="hidden flex-col items-center justify-center px-4 text-center lg:flex">
      <h2 className="text-title">No exercise selected</h2>
      <p className="mt-1 text-text-secondary">Pick an exercise to see its details.</p>
    </div>
  );
}
