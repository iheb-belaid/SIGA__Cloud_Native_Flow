function confirmDelete() {
    return confirm("Es-tu sûr de vouloir supprimer cette tâche ?");
}

["todo", "doing", "done"].forEach(col => {
    new Sortable(document.getElementById(col), {
        group: "tasks",
        animation: 150,
        onAdd: e => {
            const taskId   = e.item.dataset.id;
            const newStat  = e.to.dataset.status;
            fetch(`/tasks/${taskId}/status/${newStat}`, { method: "PATCH" });
        }
    });
});
