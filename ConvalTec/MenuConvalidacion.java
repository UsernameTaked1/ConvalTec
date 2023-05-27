import java.awt.BorderLayout;
import java.awt.Color; 
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MenuConvalidacion extends JFrame {
    private JPanel mate, table, i, d, botones;
    private JButton nueva, continuar, eliminar;
    private String no_control;
    private JComboBox <Object> alumnos;
    private SQLConexion sql;
    private JLabel fondo;
    private ImageIcon imageIcon;
    private Icon icono;


    public MenuConvalidacion() throws SQLException {

        sql = new SQLConexion();

        fondo = new JLabel("hola");
        nueva = new JButton("Nueva Convalidación");
        continuar = new JButton("Continuar Convalidación");
        eliminar = new JButton("Eliminar Alumno");

        alumnos = new JComboBox<>();

        botones = new JPanel();
        i = new JPanel();
        d = new JPanel();
        table = new JPanel(new FlowLayout()); // Utilizamos FlowLayout para los botones
        mate = new JPanel();

         // Carga la imagen desde un archivo
         System.out.println(System.getProperty("user.dir")+"\\imágenes\\fondo.png");
         imageIcon = new ImageIcon(System.getProperty("user.dir")+"\\imágenes\\fondo.png");

        atributos();
        armado();
        sql.conectar();
        inicializar();
        escuchas();
    }

    public void atributos() {
        setTitle("ConvalTEC");
        setSize(683, 679);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        mate.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        mate.setBackground(new Color(255,248,231));
        botones.setPreferredSize(new Dimension(0, 100));
        botones.setBackground(new Color(96, 97, 99));
        setResizable(false);

        table.setBackground(new Color(220, 220, 220));
        table.setLayout(new BoxLayout(table, BoxLayout.Y_AXIS));
        table.setPreferredSize(new Dimension(479, 483));
        table.setAlignmentX(Component.CENTER_ALIGNMENT); 

        i.setPreferredSize(new Dimension(100, 0));
        i.setBackground(new Color(27, 57, 106));
        
        d.setPreferredSize(new Dimension(100, 0));
        d.setBackground(new Color(27, 57, 106));

        botones.setOpaque(true);
        i.setOpaque(false);
        d.setOpaque(false);
        table.setOpaque(false); 
        mate.setOpaque(false);

        fondo.setBackground( new Color(0, 0, 0));
        fondo.setOpaque(true);
        fondo.setBounds(0,0, 683, 583);
            // Establece la imagen en el JLabel
        icono = new ImageIcon(imageIcon.getImage().getScaledInstance(fondo.getWidth(), fondo.getHeight(), Image.SCALE_SMOOTH));
        fondo.setIcon(icono);
    }

    public void armado() {
        add(table, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        add(fondo);

        botones.add(nueva);
        botones.add(continuar);
        botones.add(eliminar);
    }

    public void escuchas() {
        nueva.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {       
                setVisible(false);
                Alumno a;
                try {
                    a = new Alumno();
                    a.mostrar(true);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Error al realizar la conexión con la base de datos:\n"+e1.getMessage());
                }
                
            }
            
        });
        continuar.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                JFrame f = new JFrame();
                JPanel jCB = new JPanel();
                JButton volver = new JButton("Volver");
                JButton Continuar = new JButton("Continuar");
                f.setDefaultCloseOperation(EXIT_ON_CLOSE);
                f.setSize(552,225);
                f.add(jCB);
                jCB.add(alumnos);
                jCB.add(Continuar,BorderLayout.LINE_END);
                jCB.add(volver,BorderLayout.LINE_END);
                f.setVisible(true);
                volver.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        f.setVisible(false);
                        setVisible(true);
                    }
                    
                });
                Continuar.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                        f.setVisible(false);
                            no_control = alumnos.getSelectedItem()+"";
                            no_control = no_control.trim();
                            Materias m;
                            try {
                                m = new Materias(no_control);
                                m.mostrar(true);
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                            
                    }
                    
                });
            }
            
        });
        eliminar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                JFrame f = new JFrame();
                JPanel jCB = new JPanel();
                JButton volver = new JButton("Volver");
                JButton Continuar = new JButton("Eliminar Alumno");
                f.setDefaultCloseOperation(EXIT_ON_CLOSE);
                f.setSize(552,225);
                f.add(jCB);
                jCB.add(alumnos);
                jCB.add(Continuar,BorderLayout.LINE_END);
                jCB.add(volver,BorderLayout.LINE_END);
                f.setVisible(true);
                volver.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        f.setVisible(false);
                        setVisible(true);
                    }
                });
                Continuar.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //agregar sentencia de borrar
                        try {
                            System.out.println("delete from alumno where no_control = '"+alumnos.getSelectedItem()+"'");
                            sql.execute("delete from convalidaciones where no_control = '"+alumnos.getSelectedItem()+"'");
                            sql.execute("delete from cursa where numero_control = '"+alumnos.getSelectedItem()+"'");
                            sql.execute("delete from alumno where no_control = '"+alumnos.getSelectedItem()+"'");
                            sql.consultaJCB("Select no_control from Alumno", alumnos);
                            inicializar();
                        } catch (SQLException e1) {
                            JOptionPane.showMessageDialog(null, "Error al eliminar al Alumno:\n" + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    
                });

            }
            
        });
    }
   
    public void mostrar(Boolean b) {
        setVisible(b);
        repaint();

    }

    public void inicializar() throws SQLException{
        sql.consultaJCB("Select no_control from Alumno", alumnos);
    }
    public static void main(String[] args) {
        try {
            MenuConvalidacion m = new MenuConvalidacion();
            m.mostrar(true);
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Error al realizar la conexión con la base de datos:\n" + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

}