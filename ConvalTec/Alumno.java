
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Alumno extends JFrame{
    private JPanel nombreP, apellidoPP, apellidoMP, noControlP, semestreP, carreraP;
    private SQLConexion sql;
    private JTextField nombre, apellidoP, apellidoM, noControl;
    private JComboBox<Integer> semestre, carrera;
    private JButton siguiente, volver;
    private JPanel table, mate, i, d, botones;
    private Integer semestres[] = {0,1,2,3,4,5,6,7,8,9,10,11,12};

    public Alumno() throws SQLException{
        sql = new SQLConexion();

        i = new JPanel();
        d = new JPanel();
        table = new JPanel();
        mate = new JPanel();
        carreraP = new JPanel();
        semestreP = new JPanel();
        noControlP = new JPanel();
        nombreP = new JPanel();
        apellidoPP = new JPanel();
        apellidoMP = new JPanel();
        botones = new JPanel();

        nombre = new JTextField("");
        apellidoP = new JTextField("");
        apellidoM = new JTextField("");
        noControl = new JTextField("");

        siguiente = new JButton("Agregar Alumno Nuevo");
        volver = new JButton("Volver");
        
        semestre = new JComboBox<>(semestres);
        carrera = new JComboBox<>();

        atributos();
        armado();
        escuchas();
        sql.conectar();
        inicializar();
    }

    private void atributos(){
        setTitle("ConvalTEC");
        setSize(600,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setResizable(false);

        nombreP.setOpaque(false);

        mate.setPreferredSize(new Dimension(0, 100));
        
        table.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // FlowLayout centrado con espacio horizontal y vertical de 10 píxeles
        table.setPreferredSize(new Dimension(559,483));
        
        nombre.setColumns(15);
        noControl.setColumns(15);
        apellidoM.setColumns(15);
        apellidoP.setColumns(15);
        
        i.setPreferredSize(new Dimension(45, 0));
        d.setPreferredSize(new Dimension(45, 0));
        i.setBackground(new Color(240,240,240));
        d.setBackground(new Color(240,240,240));
        
        
        table.setOpaque(false);
        carreraP.setOpaque(false);
        semestreP.setOpaque(false);
        noControlP.setOpaque(false);
        nombreP.setOpaque(false);
        apellidoPP.setOpaque(false);
        apellidoMP.setOpaque(false);
        
        
        
        botones.setBackground(new Color(96, 97, 99));
        mate.setBackground(new Color (240,240,240));
        table.setBackground(new Color(215,208,191));
    }

    private void armado(){
        add(mate, BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);
        add(i, BorderLayout.WEST);
        add(d,BorderLayout.EAST);
        add(botones, BorderLayout.SOUTH);

        JLabel carreraCursada =  new JLabel("Carrera que cursa el alumno:");
        carreraP.add(carreraCursada);
        carreraP.add(carrera);
        JLabel semestrelbl =  new JLabel("Semestre que está cursando el alumno:");
        semestreP.add(semestrelbl);
        semestreP.add(semestre);
        JLabel numeroControlLbl =  new JLabel("Numero de control del alumno:");
        noControlP.add(numeroControlLbl);
        noControlP.add(noControl);
        JLabel nombrelbl =  new JLabel("Nombre del alumno:");
        nombreP.add(nombrelbl);
        nombreP.add(nombre);
        JLabel apellidoPLbl =  new JLabel("Apellido paterno:");
        apellidoPP.add(apellidoPLbl);
        apellidoPP.add(apellidoP);
        JLabel apellidoMLbl =  new JLabel("Apellido materno:");
        apellidoMP.add(apellidoMLbl);
        apellidoMP.add(apellidoM);
        table.add(carreraP);
        table.add(semestreP);
        table.add(noControlP);
        table.add(nombreP);
        table.add(apellidoPP);
        table.add(apellidoMP);
        table.add(Box.createVerticalStrut(10)); // Espaciado vertical entre componentes
        botones.add(siguiente);
        botones.add(volver);
    }

    private void escuchas(){
        siguiente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(noControl.getText().length() == 8){
                    try{
                        Integer.parseInt(noControl.getText());
                        setVisible(false);
                        sql.execute("Insert into alumno values('" + noControl.getText().trim() + "','"
                                + nombre.getText().trim() + "','" + apellidoP.getText().trim() + "','"
                                + apellidoM.getText().trim() + "','" + semestre.getSelectedItem() + "','"
                                + carrera.getSelectedItem() + "')");
                        Materias m = new Materias(noControl.getText());
                        m.mostrar(true);
                    }catch(Exception numControl){
                        if (((SQLException) numControl).getErrorCode() == 2627) {
                            JOptionPane.showMessageDialog(null, "Error al registrar al alumno\nError: Esta alumno ya está registrado");
                        } else {
                            JOptionPane.showMessageDialog(null, "Por favor asegúrese de que el campo \"Número de Control\" sólo contenga números.");
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Error al ingresar el campo \"Número de Control\".");
                }
               
            }
            
        });
        volver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                MenuConvalidacion m;
                try {
                    m = new MenuConvalidacion();
                    m.mostrar(true);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
    }
  
    public void inicializar() throws SQLException {
        sql.consultaJCB("select distinct carrera from materias", carrera);
    }
    
    public void mostrar(Boolean b){
        setVisible(b);
        pack();
        repaint();
    }
}