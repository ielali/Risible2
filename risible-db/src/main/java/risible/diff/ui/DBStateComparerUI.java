package risible.diff.ui;

import risible.diff.DBStateComparer;
import risible.diff.RisibleDbDriver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBStateComparerUI {

  public static void main(String[] args) throws InstantiationException, IllegalAccessException {
    try {
      String envPath = "src/java/environments/";
      String configName = "props";

      if (args.length > 0) {
        configName = args[0];
      }
      if (args.length > 1) {
        envPath = args[1];
      }
      System.out.println("tips: arguments  are optional, first one is configName and second one is the path of the config files");
      DBStateComparerUI dbStateComparerUI = new DBStateComparerUI();
      dbStateComparerUI.createDbComparerWindowOn(envPath, configName);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void createDbComparerWindowOn(String envPath, String configName) throws SQLException, ClassNotFoundException, IOException, IllegalAccessException, InstantiationException {
    Properties properties = new Properties();
    properties.load(new FileInputStream(envPath + configName + ".properties"));

    JFrame frame = new JFrame("Track changes of db " + configName);
    Panel panel = new Panel();
    frame.add(panel);

    final DBStateComparer dbStateComparer = new DBStateComparer();
    dbStateComparer.setDbConnection(getConnection(properties));
    //dbStateComparer.loadRemarkableQueries(properties.getProperty("remarkable.queries.file"));
    if(properties.get("dbStateComparer.scan.otherSchemas")!=null) {
      dbStateComparer.setSchemasToScan(risible.util.Lists.build(((String)properties.get("dbStateComparer.scan.otherSchemas")).split(",")));
    }
    RisibleDbDriver risibleDbDriver = (RisibleDbDriver) Class.forName((String)properties.get("risible.db.driver")).newInstance();
    dbStateComparer.setRisibleDbDriver(risibleDbDriver);
    
    panel.add(createFirstStateRecorder(dbStateComparer, "Record first state"));
/*    panel.add(createFirstStateSaver(dbStateComparer, "Save first state"));
    panel.add(createFirstStateLoader(dbStateComparer, "Load first state"));
*/
    panel.add(createSecondStateRecorder(dbStateComparer, "Record second state"));
    panel.add(createDisplayDiff(dbStateComparer, "Display diff result"));
    Label resultLabel = new Label("                                                  ");
    panel.add(resultLabel);
    dbStateComparer.setUIDisplay(resultLabel);

    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    frame.pack();
    frame.setVisible(true);
  }

  private static Connection getConnection(Properties properties) throws SQLException, ClassNotFoundException {
    return getConnection(properties.getProperty("datasource.user"),
        properties.getProperty("datasource.password"),
        properties.getProperty("datasource.url"),
        properties.getProperty("datasource.driver")
    );
  }

  private static Connection getConnection(String user, String pass, String url, String className) throws SQLException, ClassNotFoundException {
    Class.forName(className);
    return DriverManager.getConnection(url, user, pass);
  }

  private static Component createDisplayDiff(final DBStateComparer dbStateComparer, String buttonLabel) {
    return createButton(buttonLabel, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dbStateComparer.diffRecords();
        System.out.println(dbStateComparer.outputVerifyTableCount());
      }
    });
  }

  private static Component createSecondStateRecorder(final DBStateComparer dbStateComparer, String buttonLabel) {
    return createButton(buttonLabel, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          dbStateComparer.secondRecord();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
      }
    });
  }

  private static Button createFirstStateRecorder(final DBStateComparer dbStateComparer, String buttonLabel) {
    return createButton(buttonLabel, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          dbStateComparer.firstRecord();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
      }
    });
  }

  private static Button createButton(String buttonLabel, ActionListener listener) {
    Button buttonFirstState = new Button(buttonLabel);
    buttonFirstState.addActionListener(listener);
    return buttonFirstState;
  }
}
