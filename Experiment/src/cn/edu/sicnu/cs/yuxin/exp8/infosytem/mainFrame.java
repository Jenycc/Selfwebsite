package cn.edu.sicnu.cs.yuxin.exp8.infosytem;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class mainFrame extends JFrame {
    public static void main(String[] args) {
        new mainFrame();
    }

    public mainFrame() {
        isNew = true;

        infoNode = new DefaultMutableTreeNode("00_学院信息");
        infoTree.setModel(new DefaultTreeModel(infoNode));
        loadInfo();

        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        idCodeLabel.setText("学院代码：");
        nameLabel.setText("学院名称：");

        setTitle("四川师范大学-学院信息系统");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);//屏幕居中
        setVisible(true);
        setMinimumSize(new Dimension(screenWidth / 3 - 100, screenHeight / 3 - 50));
        setMaximumSize(new Dimension(screenWidth, screenHeight));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveButtonActionPerformed(actionEvent);
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addButtonActionPerformed(actionEvent);
            }
        });
        infoTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                treeNodeSelectionListener(treeSelectionEvent);
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                deleteButtonActionPerformed(actionEvent);
            }
        });
    }

    public void loadInfo() {
        SchoolDatabase database = new SchoolDatabase();
        ArrayList<School> schools;
        idCodeTextField.setText("");
        nameTextField.setText("");
        stateCheckBox.setSelected(false);
        if (database.connect()) {
            schools = database.getAll();
            infoNode.removeAllChildren();
            for (School school : schools) {
                DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(String.valueOf(school.getIdentificationCode()) + "_" + school.getName());
                infoNode.add(treeNode);
                //System.out.println(String.valueOf(school.getIdentificationCode()) + "," + school.getName() + "," + String.valueOf(school.getState()));
            }
            infoTree.updateUI();
            database.close();
        } else {
            JOptionPane.showMessageDialog(this, "数据库连接失败！", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void saveButtonActionPerformed(ActionEvent actionEvent) {
        String idCode = idCodeTextField.getText();
        String name = nameTextField.getText();
        int state = stateCheckBox.isSelected() ? 1 : 0;
        if (idCode.equals("")) {
            JOptionPane.showMessageDialog(this, "请输入学院编号!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (idCode.length() > 10) {
            JOptionPane.showMessageDialog(this, "学院代码不合法!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (name.equals("")) {
            JOptionPane.showMessageDialog(this, "请输入学院名称!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        School school = new School(idCode.toCharArray(), name, state);
        SchoolDatabase database = new SchoolDatabase();
        if (database.connect()) {
            if (database.select(school.getIdentificationCode()) != null && !isNew) {
                if (database.update(school)) {
                    JOptionPane.showMessageDialog(this, "修改成功！");
                } else {
                    JOptionPane.showMessageDialog(this, "修改失败！");
                    return;
                }
            } else {
                if (!isNew) {
                    if (JOptionPane.showConfirmDialog(this, "不存在此条信息，是否添加？", "警告", JOptionPane.YES_NO_OPTION) != 0) {
                        return;
                    }
                }
                if (database.insert(school)) {
                    JOptionPane.showMessageDialog(this, "添加成功！");
                } else {
                    JOptionPane.showMessageDialog(this, "添加失败！");
                    return;
                }
                isNew = false;
            }
            loadInfo();
            database.close();
        } else {
            JOptionPane.showMessageDialog(this, "数据库连接失败！", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void addButtonActionPerformed(ActionEvent actionEvent) {
        idCodeTextField.setText("");
        nameTextField.setText("");
        stateCheckBox.setSelected(false);
        isNew = true;
    }

    public void deleteButtonActionPerformed(ActionEvent actionEvent) {
        String idCode = idCodeTextField.getText();
        SchoolDatabase database = new SchoolDatabase();
        try {
            if (database.connect()) {
                if (JOptionPane.showConfirmDialog(this, "是否删除？", "警告", JOptionPane.YES_NO_OPTION) == 0) {
                    if (database.delete(idCode.toCharArray())) {
                        JOptionPane.showMessageDialog(this, "删除成功！");
                    } else {
                        JOptionPane.showMessageDialog(this, "删除失败！");
                    }
                    loadInfo();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void treeNodeSelectionListener(TreeSelectionEvent treeSelectionEvent) {
        isNew = false;
        String str = infoTree.getSelectionPath().getLastPathComponent().toString();
        String idCode = str.substring(0, str.indexOf('_'));
        if (idCode.equals("00")) {
            return;
        }
        School school = null;
        SchoolDatabase database = new SchoolDatabase();
        try {
            if (database.connect()) {
                if ((school = database.select(idCode.toCharArray())) != null) {
                    idCodeTextField.setText(String.valueOf(school.getIdentificationCode()));
                    nameTextField.setText(school.getName());
                    stateCheckBox.setSelected(school.getState() == 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNew;
    private JPanel mainPanel;
    private JTextField idCodeTextField;
    private JTextField nameTextField;
    private JButton saveButton;
    private JLabel idCodeLabel;
    private JLabel nameLabel;
    private JTree infoTree;
    private JButton addButton;
    private JButton deleteButton;
    private JCheckBox stateCheckBox;
    private DefaultMutableTreeNode infoNode;
}
