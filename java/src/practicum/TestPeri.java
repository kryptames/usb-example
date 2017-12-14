package practicum;

import org.usb4java.Device;

public class TestPeri
{
    public static void main(String[] args) throws Exception
    {
        McuBoard.initUsb();

        try
        {
        	Device[] devices = McuBoard.findBoards();
        	
        	if (devices.length == 0) {
                System.out.format("** Practicum board not found **\n");
                return;
        	}
        	else {
                System.out.format("** Found %d practicum board(s) **\n", devices.length);
        	}
            McuWithPeriBoard peri = new McuWithPeriBoard(devices[0]);

            System.out.format("** Practicum board found **\n");
            System.out.format("** Manufacturer: %s\n", peri.getManufacturer());
            System.out.format("** Product: %s\n", peri.getProduct());

            int count = 0;

            while (true) 
            {
                Thread.sleep(500);
                peri.setLedValue(count);
                boolean sw = peri.getSwitch();
                int light = peri.getLight();
                System.out.format("LED set to %d | Switch state: %s | Light: %d\n",
                        count, sw, light);

                count++;
                if (count > 7) count = 0;
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        McuBoard.cleanupUsb();
    }
}
