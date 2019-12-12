package sample;

// /home/korisnik/Downloads/ireasoning/mibbrowser/browser.sh

import a.a.a.a.a.a.ne;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import com.ireasoning.protocol.*;
import com.ireasoning.protocol.snmp.*;
import sun.awt.X11.XSystemTrayPeer;

import javax.swing.*;
import javax.xml.crypto.Data;
import javax.xml.ws.FaultAction;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

class DataInterpreter {
    private String interfaceName;
    private int interfaceId;
    private long inOctets;
    private long outOctets;
    private long packetInCount;
    private long packetOutCount;
    private long ifSpeed;

    public long getIfSpeed() {
        return ifSpeed;
    }

    public void setIfSpeed(long ifSpeed){
        this.ifSpeed = ifSpeed;
    }

    public long getPacketInCount() {
        return packetInCount;
    }

    public void setPacketInCount(long packetInCount) {
        this.packetInCount = packetInCount;
    }

    public long getPacketOutCount() {
        return packetOutCount;
    }

    public void setPacketOutCount(long packetOutCount) {
        this.packetOutCount = packetOutCount;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public int getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(int interfaceId) {
        this.interfaceId = interfaceId;
    }

    public long getInOctets() {
        return inOctets;
    }

    public void setInOctets(long inOctets) {
        this.inOctets = inOctets;
    }

    public long getOutOctets() {
        return outOctets;
    }

    public void setOutOctets(long outOctets) {
        this.outOctets = outOctets;
    }
}

class Socket {
    // Community string = si2019
    private static Socket singleSocket;

    private Socket(){

    }
    static public Socket getSocket(){
        if(singleSocket == null)
            singleSocket = new Socket();

        return singleSocket;
    }

    public ArrayList<DataInterpreter> getDeviceInterfaceData(String host, int port, String community, int version){
        ArrayList<DataInterpreter> dataList = new ArrayList<>();
        SnmpTarget target = new SnmpTarget(host, port, community, community, version);
        try {
            SnmpSession session = new SnmpSession(target);
            session.loadMib2();
            SnmpTableModel retPdu = session.snmpGetTable("ifTable");
            for(int i = 0; i < retPdu.getRowCount(); i++) {
                DataInterpreter dataListItem = new DataInterpreter();
                if(Integer.parseInt(retPdu.getValueAt(i, 7).toString()) == 1) {
                    dataListItem.setInterfaceId(Integer.parseInt(retPdu.getValueAt(i, 0).toString()));
                    dataListItem.setInterfaceName(retPdu.getValueAt(i, 1).toString());
                    dataListItem.setInOctets(Long.parseLong(retPdu.getValueAt(i, 9).toString()));
                    dataListItem.setOutOctets(Long.parseLong(retPdu.getValueAt(i, 15).toString()));
                    dataListItem.setPacketInCount(Long.parseLong(retPdu.getValueAt(i, 10).toString()));
                    dataListItem.setPacketOutCount(Long.parseLong(retPdu.getValueAt(i, 16).toString()));
                    dataListItem.setIfSpeed(Long.parseLong(retPdu.getValueAt(i, 4).toString()));
                    dataList.add(dataListItem);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }

}
class InterfaceData {
    private long inOctets;
    private long outOctets;
    private long packetInCount;
    private long packetOutCount;
    private long ifSpeed;

    public long getIfSpeed() {
        return ifSpeed;
    }

    public void setIfSpeed(long ifSpeed){
        this.ifSpeed = ifSpeed;
    }

    public long getInOctets() {
        return inOctets;
    }

    public void setInOctets(long inOctets) {
        this.inOctets = inOctets;
    }

    public long getOutOctets() {
        return outOctets;
    }

    public void setOutOctets(long outOctets) {
        this.outOctets = outOctets;
    }

    public long getPacketInCount() {
        return packetInCount;
    }

    public void setPacketInCount(long packetInCount) {
        this.packetInCount = packetInCount;
    }

    public long getPacketOutCount() {
        return packetOutCount;
    }

    public void setPacketOutCount(long packetOutCount) {
        this.packetOutCount = packetOutCount;
    }
}
class RouterData{
    private ArrayList<InterfaceData> data;

    public RouterData(ArrayList<InterfaceData> data) {
        this.data = data;
    }

    public ArrayList<InterfaceData> getData() {
        return data;
    }

    public void setData(ArrayList<InterfaceData> data) {
        this.data = data;
    }
}
class RouterInterface{
    String interfaceName;
    int interfaceId;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public int getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(int interfaceId) {
        this.interfaceId = interfaceId;
    }
}
class Router {


    private int portNumber;
    private String communityString;
    private ArrayList<RouterInterface> interfaces;
    private String ipAddress;
    private ArrayList<RouterData> routerDataHistory;
    private ArrayList<Observer> bindCharts;
    public Router(String ipAddress, int portNumber, String communityString, ArrayList<RouterData> routerDataHistory) {
        this.portNumber = portNumber;
        this.communityString = communityString;
        this.ipAddress = ipAddress;
        this.routerDataHistory = routerDataHistory;
        interfaces = new ArrayList<>();
    }

    public ArrayList<RouterInterface> getInterfaces(){
        return interfaces;
    }
    public Router(String ipAddress){
        this(ipAddress, 161, "si2019", new ArrayList<>());
    }
    private boolean interfaceSet = false;
    public void setInterfaces() {
        if (!interfaceSet) {
            ArrayList<DataInterpreter> temp = Socket.getSocket().getDeviceInterfaceData(ipAddress, portNumber, communityString, 1);

            for (DataInterpreter dataInterpreter : temp) {
                RouterInterface routerInterface = new RouterInterface();
                routerInterface.setInterfaceName(dataInterpreter.getInterfaceName());
                routerInterface.setInterfaceId(dataInterpreter.getInterfaceId());
                interfaces.add(routerInterface);
            }
            interfaceSet = true;
        }
    }

    public void bindAreaCharts(ArrayList<Observer> list){
        if (interfaceSet) {
            if (bindCharts == null)
                bindCharts = new ArrayList<>();
            bindCharts.addAll(list);
            for (Observer bindChart : bindCharts) {
                bindChart.setSubject(this);
            }
        }
    }

    
    public void removeGraph(Observer observer){
        if (bindCharts.indexOf(observer) != -1){
            bindCharts.remove(observer);
        }
    }
    
    public void removeGraphList(ArrayList<Observer> observers){
        for (Observer observer :
                observers) {
            removeGraph(observer);
        }
    }
    
    private void updateCharts(){
        for (Observer observer:
             bindCharts) {
            observer.readData();
        }
    }
    public ArrayList<RouterData> getRouterDataHistory(){
        return routerDataHistory;
    }
    public void readData() {
        ArrayList<DataInterpreter> data = Socket.getSocket().getDeviceInterfaceData(ipAddress,portNumber,communityString,1);
        ArrayList<InterfaceData> interfaceData = new ArrayList<>();
        for (DataInterpreter dat :
                data) {
            InterfaceData interfaceData1 = new InterfaceData();
            interfaceData1.setInOctets(dat.getInOctets());
            interfaceData1.setOutOctets(dat.getOutOctets());
            interfaceData1.setPacketInCount(dat.getPacketInCount());
            interfaceData1.setPacketOutCount(dat.getPacketOutCount());
            interfaceData1.setIfSpeed(dat.getIfSpeed());

            interfaceData.add(interfaceData1);
        }
        routerDataHistory.add(new RouterData(interfaceData));
        if(routerDataHistory.size() > 100)
            routerDataHistory.remove(0);
        updateCharts();
    }


}
// Implementiraj metodu za azuriranje
abstract class Observer{
    protected Router subject;

    abstract void readData();

    public Router getSubject() {
        return subject;
    }

    public void setSubject(Router subject) {
        this.subject = subject;
    }
}
abstract class Graph extends Observer{
    protected AreaChart chart;
    final protected CategoryAxis xAxis = new CategoryAxis();
    final protected NumberAxis yAxis = new NumberAxis();
    private long time = 10;
    public long getTime() {
        return time;
    }

    public setTime(long time){
        this.time = time;
    }
    {
        xAxis.setLabel("Time/s");
        yAxis.setLabel("Value");
    }
    protected String formatSeconds (long time){
        long hours = time / 3600;
        long minutes = (time % 3600) / 60;
        long seconds = time % 60;

        String str = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return str;
    }

    protected double getThrughput(long current, long previous, long speed){
        return 8*((double)(current - previous))/(10*speed);
    }

}

// Svaki graf se azuira na svoj nacin, bilo da uzima druge podatke ili sta ja znam
class InPacketGraph extends Graph{

    public InPacketGraph(AreaChart chart) {
        this.chart = chart;

    }

    @Override
    void readData() {
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                chart.getData().clear();
            }
        });
        ArrayList<RouterData> dat = subject.getRouterDataHistory();
        ArrayList<RouterInterface> interfaces = subject.getInterfaces();
        ArrayList<XYChart.Series> seriess = new ArrayList<>();
        int numberOfDataPoints = Math.min(10, dat.size());
        int numberOfInterfaces = interfaces.size();

        for (int i = 0; i < numberOfInterfaces; i++){
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(interfaces.get(i).getInterfaceName());

            for (int j = dat.size() - numberOfDataPoints; j < dat.size(); j++){
                String secondsPast = formatSeconds((long)j*10);
                Long getPacketNumber = dat.get(j).getData().get(i).getPacketInCount();
                series.getData().add(new XYChart.Data(secondsPast, getPacketNumber));
            }
            seriess.add(series);
        }
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < seriess.size(); i++) {
                    chart.getData().add(seriess.get(i));
                }
            }
        });

    }
}
class OutPacketGraph extends Graph{
    private AreaChart chart;
    public OutPacketGraph(AreaChart chart) {
        this.chart = chart;

    }

    @Override
    void readData() {
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                chart.getData().clear();
            }
        });
        ArrayList<RouterData> dat = subject.getRouterDataHistory();
        ArrayList<RouterInterface> interfaces = subject.getInterfaces();
        ArrayList<XYChart.Series<String, Number>> seriess = new ArrayList<>();
        int numberOfDataPoints = Math.min(10, dat.size());
        int numberOfInterfaces = interfaces.size();

        for (int i = 0; i < numberOfInterfaces; i++){
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(interfaces.get(i).getInterfaceName());

            for (int j = dat.size() - numberOfDataPoints; j < dat.size(); j++){
                String secondsPast = formatSeconds((long)j*10);
                long getPacketNumber = dat.get(j).getData().get(i).getPacketOutCount();
                series.getData().add(new XYChart.Data(secondsPast, (int)getPacketNumber));
            }

            seriess.add(series);
        }

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < seriess.size(); i++) {
                    chart.getData().add(seriess.get(i));
                }
            }
        });
    }
}
class InSpeedGraph extends Graph{
    private AreaChart chart;

    public InSpeedGraph(AreaChart chart) {
        this.chart = chart;
    }

    @Override
    void readData() {
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                chart.getData().clear();
            }
        });
        ArrayList<RouterData> dat = subject.getRouterDataHistory();
        ArrayList<RouterInterface> interfaces = subject.getInterfaces();
        ArrayList<XYChart.Series<String, Number>> seriess = new ArrayList<>();
        int numberOfDataPoints = Math.min(11, dat.size());
        int numberOfInterfaces = interfaces.size();

        for (int i = 0; i < numberOfInterfaces; i++){
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(interfaces.get(i).getInterfaceName());

            for (int j = dat.size() - numberOfDataPoints + 1; j < dat.size(); j++){
                if (numberOfDataPoints == 1){
                    series.getData().add(new XYChart.Data(formatSeconds(0), 0));
                } else {
                    String secondsPast = formatSeconds((long) j * 10);
                    long currentOctets = dat.get(j).getData().get(i).getInOctets();
                    long previousOctets = dat.get(j-1).getData().get(i).getInOctets();
                    long speed = dat.get(j - 1).getData().get(i).getIfSpeed();
                    // long speed = 1;
                    double thrughput = getThrughput(currentOctets, previousOctets, speed);
                    series.getData().add(new XYChart.Data(secondsPast, thrughput));
                }
            }

            seriess.add(series);
        }

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < seriess.size(); i++) {
                    chart.getData().add(seriess.get(i));
                }
            }
        });
    }
}
class OutSpeedGraph extends Graph{
    private AreaChart chart;

    public OutSpeedGraph(AreaChart chart) {
        this.chart = chart;

    }

    @Override
    void readData() {
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                chart.getData().clear();
            }
        });
        ArrayList<RouterData> dat = subject.getRouterDataHistory();
        ArrayList<RouterInterface> interfaces = subject.getInterfaces();
        ArrayList<XYChart.Series<String, Number>> seriess = new ArrayList<>();
        int numberOfDataPoints = Math.min(11, dat.size());
        int numberOfInterfaces = interfaces.size();

        for (int i = 0; i < numberOfInterfaces; i++){
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(interfaces.get(i).getInterfaceName());

            for (int j = dat.size() - numberOfDataPoints + 1; j < dat.size(); j++){
                if (numberOfDataPoints == 1){
                    series.getData().add(new XYChart.Data(formatSeconds(0), 0));
                } else {
                    String secondsPast = formatSeconds((long) j * 10);
                    long currentOctets = dat.get(j).getData().get(i).getOutOctets();
                    long previousOctets = dat.get(j-1).getData().get(i).getOutOctets();
                    long speed = dat.get(j - 1).getData().get(i).getIfSpeed();
                    // long speed = 1;
                    double thrughput = getThrughput(currentOctets, previousOctets, speed);
                    series.getData().add(new XYChart.Data(secondsPast, thrughput));
                }
            }

            seriess.add(series);
        }

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < seriess.size(); i++) {
                    chart.getData().add(seriess.get(i));
                }
            }
        });
    }
}


// Tajmer ima pokaivac na sve rutere i azurira ih kad dobije timeout
class Timer extends Thread {
    private ArrayList<Router> myRouters = new ArrayList<>();
    private long sleepInterval;

    public Timer(long sleepInterval){
        this.sleepInterval = sleepInterval;
        setDaemon(true);
    }
    public Timer(){
        this(10000);
    }
    private void notifyRouters(){
        for (Router r :
                myRouters) {
            r.readData();
        }
    }

    public void routerSubscribe(Router r){
        myRouters.add(r);
    }
    public void routerUnsubscribe(Router r){
        try {
            myRouters.remove(r);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                notifyRouters();
                sleep(sleepInterval);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


public class Controller {
    public ComboBox<String> routerSelect;
    public Button showStats;


    public AreaChart inputPacketGraph;
    public AreaChart outputPacketGraph;
    public AreaChart inputSpeedGraph;
    public AreaChart outputSpeedGraph;

    public AreaChart inputPacketGraphR1;
    public AreaChart outputPacketGraphR1;
    public AreaChart inputSpeedGraphR1;
    public AreaChart outputSpeedGraphR1;

    public AreaChart inputPacketGraphR2;
    public AreaChart outputPacketGraphR2;
    public AreaChart inputSpeedGraphR2;
    public AreaChart outputSpeedGraphR2;

    public AreaChart inputPacketGraphR3;
    public AreaChart outputPacketGraphR3;
    public AreaChart inputSpeedGraphR3;
    public AreaChart outputSpeedGraphR3;

    private Router router1;
    private Router router2;
    private Router router3;


    private InPacketGraph   singleInputPacketGraph  ;
    private OutPacketGraph  singleOutputPacketGraph ;
    private InSpeedGraph    singleInputSpeedGraph   ;
    private OutSpeedGraph   singleOutputSpeedGraph  ;

    private InPacketGraph   inputPacketGraphR1Object;
    private OutPacketGraph  outputPacketGraphR1Object;
    private InSpeedGraph    inputSpeedGraphR1Object;
    private OutSpeedGraph   outputSpeedGraphR1Object;

    private InPacketGraph   inputPacketGraphR2Object;
    private OutPacketGraph  outputPacketGraphR2Object;
    private InSpeedGraph    inputSpeedGraphR2Object;
    private OutSpeedGraph   outputSpeedGraphR2Object;

    private InPacketGraph   inputPacketGraphR3Object;
    private OutPacketGraph  outputPacketGraphR3Object;
    private InSpeedGraph    inputSpeedGraphR3Object;
    private OutSpeedGraph   outputSpeedGraphR3Object;

    private ArrayList<Observer> globalGraphList = new ArrayList<>();

    private ArrayList<Observer> router1List = new ArrayList<>();
    private ArrayList<Observer> router2List = new ArrayList<>();
    private ArrayList<Observer> router3List = new ArrayList<>();

    private Timer timer;

    private int selectedRouterForGlobalGraph = 0;

    @FXML
    public void initialize(){
        ObservableList<String> routers = FXCollections.observableArrayList("Router 1", "Router 2", "Router 3");
        routerSelect.setItems(routers);

        singleInputPacketGraph  = new InPacketGraph(inputPacketGraph);
        singleOutputPacketGraph = new OutPacketGraph(outputPacketGraph);
        singleInputSpeedGraph   = new InSpeedGraph(inputSpeedGraph);
        singleOutputSpeedGraph  = new OutSpeedGraph(outputSpeedGraph);

        inputPacketGraphR1Object    = new InPacketGraph(inputPacketGraphR1);
        outputPacketGraphR1Object   = new OutPacketGraph(outputPacketGraphR1);
        inputSpeedGraphR1Object     = new InSpeedGraph(inputSpeedGraphR1);
        outputSpeedGraphR1Object    = new OutSpeedGraph(outputSpeedGraphR1);

        inputPacketGraphR2Object    = new InPacketGraph(inputPacketGraphR2);
        outputPacketGraphR2Object   = new OutPacketGraph(outputPacketGraphR2);
        inputSpeedGraphR2Object     = new InSpeedGraph(inputSpeedGraphR2);
        outputSpeedGraphR2Object    = new OutSpeedGraph(outputSpeedGraphR2);

        inputPacketGraphR3Object    = new InPacketGraph(inputPacketGraphR3);
        outputPacketGraphR3Object   = new OutPacketGraph(outputPacketGraphR3);
        inputSpeedGraphR3Object     = new InSpeedGraph(inputSpeedGraphR3);
        outputSpeedGraphR3Object    = new OutSpeedGraph(outputSpeedGraphR3);

        router1List.add(inputPacketGraphR1Object    );
        router1List.add(outputPacketGraphR1Object   );
        router1List.add(inputSpeedGraphR1Object     );
        router1List.add(outputSpeedGraphR1Object    );

        router2List.add(inputPacketGraphR2Object    );
        router2List.add(outputPacketGraphR2Object   );
        router2List.add(inputSpeedGraphR2Object     );
        router2List.add(outputSpeedGraphR2Object    );

        router3List.add(inputPacketGraphR3Object    );
        router3List.add(outputPacketGraphR3Object   );
        router3List.add(inputSpeedGraphR3Object     );
        router3List.add(outputSpeedGraphR3Object    );

        timer = new Timer();
        router1 = new Router("192.168.10.1");
        router2 = new Router("192.168.20.1");
        router3 = new Router("192.168.30.1");
        router1.setInterfaces();
        router2.setInterfaces();
        router3.setInterfaces();

        globalGraphList.add(singleInputPacketGraph);
        globalGraphList.add(singleOutputPacketGraph);
        globalGraphList.add(singleInputSpeedGraph);
        globalGraphList.add(singleOutputSpeedGraph);



        router1.bindAreaCharts(router1List);
        router2.bindAreaCharts(router2List);
        router3.bindAreaCharts(router3List);

        timer.routerSubscribe(router1);
        timer.routerSubscribe(router2);
        timer.routerSubscribe(router3);

        timer.start();

    }

    public void showStatsButtonClicked(ActionEvent actionEvent) {
        int previousRouter = selectedRouterForGlobalGraph;
        selectedRouterForGlobalGraph = routerSelect.getSelectionModel().getSelectedIndex() + 1;
        if(selectedRouterForGlobalGraph != 0) {
            switch (selectedRouterForGlobalGraph) {
                case 1:
                    router1.bindAreaCharts(globalGraphList);
                    break;
                case 2:
                    router2.bindAreaCharts(globalGraphList);
                    break;
                case 3:
                    router3.bindAreaCharts(globalGraphList);
                    break;
                default:
                    System.out.println("Error");

            }
            if (previousRouter != 0) {
                switch (previousRouter) {
                    case 1:
                        router1.removeGraphList(globalGraphList);
                        break;
                    case 2:
                        router2.removeGraphList(globalGraphList);
                        break;
                    case 3:
                        router3.removeGraphList(globalGraphList);
                        break;
                    default:
                        System.out.println("Error");
                }
            }

            for (Observer observer : globalGraphList) {
                observer.readData();
            }
        }

    }
}