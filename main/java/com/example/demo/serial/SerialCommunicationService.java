package com.example.demo.serial;

import com.fazecast.jSerialComm.SerialPort;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SerialCommunicationService {

    private SerialPort serialPort;

    private static final Pattern DEFAULT_TEMPERATURE_PATTERN = Pattern.compile("Temp:\\s*(\\d+\\.\\d+)");
    private static final String JSON_TEMPERATURE_KEY = "temperature";

    public void listAvailablePorts() {
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("Tillgängliga portar:");
        for (SerialPort port : ports) {
            System.out.println(port.getSystemPortName() + ": " + port.getDescriptivePortName());
        }
    }

    public void openSerialPort() {
        listAvailablePorts();

        serialPort = SerialPort.getCommPort("COM4");
        serialPort.setComPortParameters(9600, 8, 1, 0);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 1000);  // TO 1 sek

        if (serialPort.openPort()) {
            System.out.println("Porten är öppen och redo att ta emot data på " + serialPort.getSystemPortName());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Fel vid väntan: " + e.getMessage());
            }

        } else {
            System.out.println("Kunde inte öppna porten " + serialPort.getSystemPortName());
            throw new RuntimeException("Kunde inte öppna porten. Kontrollera att porten inte används av ett annat program.");
        }
    }


    public String readSensorData() {
        StringBuilder sensorData = new StringBuilder();
        try {
            if (serialPort != null && serialPort.isOpen()) {
                System.out.println("Porten " + serialPort.getSystemPortName() + " är öppen, läser data...");

                byte[] readBuffer = new byte[1024];  // Buffert för att läsa inkommande data
                int numRead;
                long startTime = System.currentTimeMillis();
                boolean isComplete = false;

                while (System.currentTimeMillis() - startTime < 5000 && !isComplete) {
                    numRead = serialPort.readBytes(readBuffer, readBuffer.length);
                    if (numRead > 0) {
                        sensorData.append(new String(readBuffer, 0, numRead));
                        System.out.println("Rådata mottagen från seriell port: " + sensorData.toString());

                        if (sensorData.toString().contains("\n")) {
                            isComplete = true;
                        }
                    } else {
                        Thread.sleep(100);  // Vänta 100ms
                    }
                }

                System.out.println("Data mottagen från Arduino (som sträng): " + sensorData.toString());
            } else {
                System.out.println("Porten är inte öppen.");
                throw new RuntimeException("Porten är inte öppen när vi försöker läsa. Kontrollera om porten har stängts.");
            }
        } catch (Exception e) {
            System.out.println("Ett fel uppstod vid läsning av data: " + e.getMessage());
        }
        return sensorData.toString();
    }


    public double extractTemperature(String sensorData) {
        try {

            Matcher matcher = DEFAULT_TEMPERATURE_PATTERN.matcher(sensorData);
            if (matcher.find()) {
                return Double.parseDouble(matcher.group(1));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ogiltigt temperaturvärde i data: " + sensorData, e);
        }

        throw new IllegalArgumentException("Kunde inte extrahera temperatur från data: " + sensorData);
    }

    public void closeSerialPort() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            System.out.println("Seriella porten " + serialPort.getSystemPortName() + " är stängd.");
        } else {
            System.out.println("Porten var redan stängd.");
        }
    }
}
