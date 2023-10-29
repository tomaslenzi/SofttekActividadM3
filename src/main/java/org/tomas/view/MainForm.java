package org.tomas.view;

import org.tomas.connection.DataBaseConnection;
import org.tomas.todolist.Task;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainForm {
    private JPanel rootPanel;
    private JButton addTaskBtn;
    private JButton deleteTaskBtn;
    private JButton editTaskBtn;
    private JTable table1;
    private JButton showCompletedTasksBtn;
    private JButton showIncompletedTasksBtn;


    private List<Task> tasks;
    private TaskTableModel taskTableModel;

    private DataBaseConnection databaseConnection;

    public MainForm() {

        // Inicialización
        tasks = new ArrayList<>();
        databaseConnection = new DataBaseConnection();
        taskTableModel = new TaskTableModel(tasks, databaseConnection);
        table1.setModel(taskTableModel);

        loadAllTasks(); // Llama al método para cargar todas las tareas

        // Configuración de botones
        addTaskBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewTask();
            }
        });

        deleteTaskBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedTask();
            }
        });

        editTaskBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedTask();
            }
        });
        showCompletedTasksBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Acción para mostrar tareas completadas en una ventana
                List<Task> completedTasks = getCompletedTasks();
                displayTasksInDialog(completedTasks, "Tareas Completadas");

            }
        });
        showIncompletedTasksBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Acción para mostrar tareas incompletas en una ventana
                List<Task> incompleteTasks = getIncompleteTasks();
                displayTasksInDialog(incompleteTasks, "Tareas Incompletas");

            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);//para que inicie en el centro
    }

    /**
     * Método para obtener tareas completadas
     */
    private List<Task> getCompletedTasks() {
        List<Task> completedTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isCompleted()) {
                completedTasks.add(task);
            }
        }
        return completedTasks;
    }

    /**
     * Método para obtener tareas incompletas
     */
    private List<Task> getIncompleteTasks() {
        List<Task> incompleteTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (!task.isCompleted()) {
                incompleteTasks.add(task);
            }
        }
        return incompleteTasks;
    }

    /**
     * * Método para mostrar tareas completas o incompletas en una ventana
     */
    private void displayTasksInDialog(List<Task> tasksToShow, String title) {
        if (tasksToShow.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay tareas para mostrar.", title, JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder message = new StringBuilder();
            for (Task task : tasksToShow) {
                message.append("Nombre: ").append(task.getName()).append("\n");
                message.append("Descripción: ").append(task.getDescription()).append("\n\n");
            }
            JOptionPane.showMessageDialog(null, message.toString(), title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Metodo para cargar todas las tareas
     */
    private void loadAllTasks() {
        // Llama al método de DataBaseConnection para cargar todas las tareas
        List<Task> loadedTasks = databaseConnection.getAllTasks();

        // Actualiza la lista de tareas y la tabla
        tasks.clear();
        tasks.addAll(loadedTasks);
        taskTableModel.fireTableDataChanged();
    }

    /**
     * metodo para cargar una tarea
     */
    private void addNewTask() {
        do {
            // Crea un panel personalizado con campos de entrada
            JPanel inputPanel = new JPanel(new GridLayout(3, 2));
            JTextField nombreField = new JTextField(20);
            JTextArea descripcionArea = new JTextArea(5, 20);

            inputPanel.add(new JLabel("Nombre de la Tarea:"));
            inputPanel.add(nombreField);
            inputPanel.add(new JLabel("Descripción:"));
            inputPanel.add(new JScrollPane(descripcionArea));

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Agregar Tarea", JOptionPane.OK_CANCEL_OPTION);

            try {
                if (result == JOptionPane.OK_OPTION) {
                    // Leer los valores ingresados
                    String nombre = nombreField.getText();
                    String descripcion = descripcionArea.getText();

                    if (nombre.isEmpty()) {
                        throw new IllegalArgumentException("El nombre de la tarea no puede estar vacío.");
                    }

                    // Crear un objeto de la clase Task con los valores obtenidos
                    Task nuevaTarea = new Task(nombre, descripcion);

                    // Se agrega a la lista
                    tasks.add(nuevaTarea);
                    // Actualiza la tabla
                    taskTableModel.fireTableDataChanged();

                    // Agregar la tarea a la base de datos utilizando DataBaseConnection
                    databaseConnection.addTask(nuevaTarea);

                    // Muestra una ventana de diálogo para preguntar si desea agregar otra tarea
                    int repeatResult = JOptionPane.showConfirmDialog(null, "¿Desea agregar otra tarea?", "Tarea agregada con éxito!", JOptionPane.YES_NO_OPTION);

                    if (repeatResult == JOptionPane.NO_OPTION) {
                        // Si el usuario hace clic en "No", se detiene el bucle
                        break;
                    }
                } else {
                    // Si el usuario hace clic en "Cancelar" o cierra el diálogo, se termina el bucle
                    break;
                }
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error de entrada", JOptionPane.ERROR_MESSAGE);
            }
        } while (true);
    }

    /**
     * metodo para eliminar una tarea seleccionada
     */
    private void deleteSelectedTask() {
        // Lógica para eliminar una tarea
        int selectedRow = table1.getSelectedRow();
        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay ninguna tarea cargada para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } else if (selectedRow != -1) {
            Task selectedTask = tasks.get(selectedRow); // Obtiene la tarea seleccionada

            tasks.remove(selectedRow);
            // Actualiza la tabla
            taskTableModel.fireTableDataChanged();

            // Llama al método de eliminación en la instancia de DataBaseConnection usando el ID de la tarea seleccionada
            int taskIdToDelete = selectedTask.getId();
            databaseConnection.deleteTask(taskIdToDelete);
            // Mostrar un mensaje de éxito
            JOptionPane.showMessageDialog(null, "Tarea eliminada con éxito.", "Tarea eliminada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, selecciona una tarea para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * metodo para editar una tarea seleccionada
     */
    private void editSelectedTask() {
        int selectedRow = table1.getSelectedRow();

        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay ninguna tarea cargada para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } else if (selectedRow != -1) {
            Task taskToEdit = tasks.get(selectedRow);

            //crea un panel personalizado con campos de entrada
            JPanel editPanel = new JPanel(new GridLayout(3, 2));
            JTextField nombreField = new JTextField(taskToEdit.getName());
            JTextArea descripcionArea = new JTextArea(taskToEdit.getDescription(), 5, 20);

            editPanel.add(new JLabel("Nombre de la Tarea:"));
            editPanel.add(nombreField);
            editPanel.add(new JLabel("Descripción:"));
            editPanel.add(new JScrollPane(descripcionArea));

            int result = JOptionPane.showConfirmDialog(null, editPanel, "Editar Tarea", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                // Lee los valores editados
                String nuevoNombre = nombreField.getText();
                String nuevaDescripcion = descripcionArea.getText();

                if (nuevoNombre.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El nombre de la tarea no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Actualiza la tarea con los nuevos valores
                    taskToEdit.setName(nuevoNombre);
                    taskToEdit.setDescription(nuevaDescripcion);

                    // Actualiza la tabla
                    taskTableModel.fireTableDataChanged();
                    // Actualiza la tarea en la base de datos
                    databaseConnection.updateTask(taskToEdit);
                    // Mostrar un mensaje de éxito
                    JOptionPane.showMessageDialog(null, "Tarea editada con éxito.", "Tarea editada", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, selecciona una tarea para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Clase interna que implementa un modelo de tabla personalizado para mostrar y gestionar las tareas.
     */
    public static class TaskTableModel extends AbstractTableModel {

        private final String[] COLUMNS = {"Nombre", "Descripcion", "Completada"};
        private List<Task> tasks;

        private DataBaseConnection databaseConnection;

        private TaskTableModel(List<Task> tasks, DataBaseConnection dbConnection) {
            this.tasks = tasks;
            this.databaseConnection = dbConnection;
        }

        @Override
        public int getRowCount() {
            return tasks.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return switch (columnIndex) {
                // case 0 -> tasks.get(rowIndex).getId();
                case 0 -> tasks.get(rowIndex).getName();
                case 1 -> tasks.get(rowIndex).getDescription();
                case 2 -> tasks.get(rowIndex).isCompleted();
                default -> "-";
            };
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 2) { // Para la columna "Completada"
                return Boolean.class; // Utiliza Boolean para las casillas de verificación
            } else {
                return super.getColumnClass(columnIndex);
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 2; // Hace que la columna "Completada" sea editable
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            if (columnIndex == 2) { // Si se edita la columna "Completada"
                boolean isCompleted = (boolean) value; // Obtiene el valor de la casilla de verificación

                // Obtiene la tarea correspondiente
                Task task = tasks.get(rowIndex);

                // Establece el estado de la tarea como completada o no completada segun el valor de la casilla de verificación
                task.setCompleted(isCompleted);

                // Actualiza la base de datos con el nuevo estado completado
                int taskId = task.getId();
                databaseConnection.updateTaskCompletion(taskId, isCompleted);

                // notifica a la tabla que los datos cambiaron
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }
}
