import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Materias extends JFrame{  
    private DefaultTableModel tableModel;
    private JTable tmaterias;
    private JTextField cal; 
    private JComboBox<Object> materias;
    private JPanel mate, table, i, d, botones;   
    private JButton amateria, subir, eliminar, volver; 
    private JLabel infoAlumno;
    private String no_control;
    private SQLConexion sql;
    private String carrera; 


    public Materias(String num_control) throws SQLException {
        no_control = num_control;


        sql = new SQLConexion();

        infoAlumno= new JLabel();

        botones = new JPanel();
        i = new JPanel();
        d = new JPanel();
        table = new JPanel();
        mate = new JPanel();

        cal = new JTextField();

        eliminar = new JButton("Eliminar materia");
        amateria = new JButton("Agregar");
        subir = new JButton("Continuar");
        volver = new JButton("Volver");

        tmaterias = new JTable();
        
        materias = new JComboBox<>();

        atributos();
        armado();
        escuchas();
        sql.conectar();
        carrera = sql.printConsulta("(SELECT carrera FROM alumno WHERE no_control = '" + no_control + "')");
        carrera = carrera.trim();
        inicializar();
    }

    private void atributos(){
        setTitle("ConvalTEC");
        setSize(300,300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);

        mate.setPreferredSize(new Dimension(0, 100));

        table.setBackground(new Color (215,208,191));
        table.setPreferredSize(new Dimension(479,483));
        
        i.setPreferredSize(new Dimension(100, 0));
        d.setPreferredSize(new Dimension(100, 0));

        cal.setColumns(3);

        botones.setBackground(new Color(96, 97, 99));
        mate.setBackground(new Color (240,240,240));
        table.setBackground(new Color(215,208,191));

        tmaterias.setEnabled(false);
    }

    private void armado(){
        add(mate, BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);
        add(i, BorderLayout.WEST);
        add(d,BorderLayout.EAST);
        add(botones, BorderLayout.SOUTH);

        JLabel materiasCarrera =  new JLabel("Materias de la carrera cursada:");
        mate.add(materiasCarrera);
        mate.add(materias);
        JLabel calificacion =  new JLabel("Calificación:");
        mate.add(calificacion);
        mate.add(cal);
        mate.add(amateria);
        mate.add(infoAlumno);

        table.add(tmaterias, BorderLayout.CENTER);
        table.add(new JScrollPane(tmaterias),BorderLayout.NORTH);
        botones.add(subir,BorderLayout.PAGE_END);
        botones.add(eliminar,BorderLayout.LINE_END);
        botones.add(volver);
    }

    private void escuchas(){
        subir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Convalidacion c;
                try {
                    c = new Convalidacion(no_control);
                    c.mostrar(true);
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(null, "Error al ingresar el campo:"+e1.getMessage());
                }
                setVisible(false);
            }
            
        });
        eliminar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                JFrame f = new JFrame();
                JPanel jCB = new JPanel();
                JComboBox<Object> materiasa = new JComboBox<>();
                JButton volver = new JButton("Volver");
                JButton eliminar = new JButton("eliminar");
                try {
                    sql.consultaJCB("select nombre from materias where no_materia in (select distinct clave_materia from Cursa where numero_control = '" + no_control + "')", materiasa);
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                f.setSize(552,225);
                f.setDefaultCloseOperation(EXIT_ON_CLOSE);
                f.add(jCB);
                jCB.add(materiasa);
                jCB.add(eliminar,BorderLayout.LINE_END);
                jCB.add(volver,BorderLayout.LINE_END);
                f.setVisible(true);
                volver.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        f.setVisible(false);
                        setVisible(true);
                    }
                    
                }); 
                eliminar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String num = sql.printConsulta("select no_materia from materias where nombre = '"+materiasa.getSelectedItem()+"' and carrera = '"+carrera+"'");
                            num = num.trim();
                            System.out.println(num+"            "+"select no_materia from materias where nombre = '"+materiasa.getSelectedItem()+"' and carrera = '"+carrera+"'");
                            sql.execute("Delete from cursa where cursa.clave_materia = '"+num+"' and cursa.numero_control = '"+no_control+"'");
                            sql.consultaJCB("select nombre from materias where no_materia in (select distinct clave_materia from Cursa where numero_control = '" + no_control + "')", materiasa);
                            inicializar();
                        } catch (SQLException e1) {
                            JOptionPane.showMessageDialog(null, "Error al eliminar la materia.\n Error:"+e1.getMessage());
                        }
                    }
                });
            }
        });
        volver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                MenuConvalidacion menu;
                try {
                    menu = new MenuConvalidacion();
                    menu.mostrar(true);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
        amateria.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float calificacion = 0;
                // agregar materia y actualizar jtable
                    if (cal.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Por favor ingrese la calificación de la materia.\n Error:" );
                    } else {
                        try {
                        calificacion = Float.parseFloat(cal.getText());
                        if(calificacion>=70){
                            String num = sql.printConsulta("select no_materia from materias where nombre = '"
                                + materias.getSelectedItem() + "' and carrera = '" + carrera + "'");
                        num = num.trim();
                        sql.execute("insert into Cursa values('" + no_control + "','" + num + "',"
                                + Float.parseFloat(cal.getText()) + ")");
                        tmaterias.setModel(sql.generarTabla(
                            "select nombre as 'Nombre de la materia', clave_materia as 'Clave de la materia' from materias where no_materia in (select distinct clave_materia from Cursa where numero_control = '" + no_control + "')"));
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "No puede ingresar una materia con una calificación menor de 70.\n");
                        }
                    } catch (SQLException e1) {
                        if (e1.getErrorCode() == 2627) {
                            JOptionPane.showMessageDialog(null, "Error al agregar la materia\nError: Esta materia ya ha sido agregada");
                        } else {
                            JOptionPane.showMessageDialog(null, "Error al ingresar la materia.\n" + e1.getErrorCode());
                        }
                    }
                    }
                }
            });
        }
                        
    public void inicializar() throws SQLException {
        sql.consultaJCB("select nombre from materias where carrera = (SELECT carrera FROM alumno WHERE no_control = '" + no_control + "') order by nombre asc", materias);
        infoAlumno.setText(((String)sql.printConsulta("Select nombre, apellidoP, apellidoM, no_control from alumno where no_control = '" + no_control + "'")));
        tableModel = (sql.generarTabla("select nombre as 'Nombre de la materia', clave_materia as 'Clave de la materia' from materias where no_materia in (select distinct clave_materia from Cursa where numero_control = '" + no_control + "')"));
        tmaterias.setModel(tableModel);
    }

    public void mostrar(Boolean b) {
        setVisible(b);
        pack();
        repaint();
    }
}