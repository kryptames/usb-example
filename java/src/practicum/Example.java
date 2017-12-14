package practicum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

/**
 * Controls a USB missile launcher (Only compatible with Vendor/Product
 * 1130:0202).
 * 
 * @author Klaus Reimer <k@ailis.de>
 */
public class Example
{
    /** The vendor ID of the missile launcher. */
    private static final short VENDOR_ID = 0x16c0;

    /** The product ID of the missile launcher. */
    private static final short PRODUCT_ID = 0x05dc;

    /** The USB communication timeout. */
    private static final int TIMEOUT = 1000;

    /**
     * Searches for the missile launcher device and returns it. If there are
     * multiple missile launchers attached then this simple demo only returns
     * the first one.
     * 
     * @return The missile launcher USB device or null if not found.
     */
    public static Device findBoard()
    {
        // Read the USB device list
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(null, list);
        if (result < 0)
        {
            throw new RuntimeException(
                "Unable to get device list. Result=" + result);
        }

        try
        {
            // Iterate over all devices and scan for the missile launcher
            for (Device device: list)
            {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result < 0)
                {
                    throw new RuntimeException(
                        "Unable to read device descriptor. Result=" + result);
                }
                if (descriptor.idVendor() == VENDOR_ID
                    && descriptor.idProduct() == PRODUCT_ID) return device;
            }
        }
        finally
        {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, false);
        }

        // No missile launcher found
        return null;
    }

    /**
     * Sends a message to the missile launcher.
     * 
     * @param handle
     *            The USB device handle.
     * @param message
     *            The message to send.
     */
    public static void sendMessage(DeviceHandle handle, byte[] message)
    {
        ByteBuffer buffer = ByteBuffer.allocateDirect(message.length);
        buffer.put(message);
        buffer.rewind();
        int transfered = LibUsb.controlTransfer(handle,
            (byte) (LibUsb.REQUEST_TYPE_CLASS | LibUsb.RECIPIENT_INTERFACE),
            (byte) 0x09, (short) 2, (short) 1, buffer, TIMEOUT);
        if (transfered < 0)
            throw new LibUsbException("Control transfer failed", transfered);
        if (transfered != message.length)
            throw new RuntimeException("Not all data was sent to device");
    }
    /**
     * Main method.
     * 
     * @param args
     *            Command-line arguments (Ignored)
     */
    public static void main(String[] args)
    {
        // Initialize the libusb context
        int result = LibUsb.init(null);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Search for the missile launcher USB device and stop when not found
        Device device = findBoard();
        if (device == null)
        {
            System.err.println("Missile launcher not found.");
            System.exit(1);
        }

        // Open the device
        DeviceHandle handle = new DeviceHandle();
        result = LibUsb.open(device, handle);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to open USB device", result);
        }
        try
        {
            // Check if kernel driver is attached to the interface
            int attached = LibUsb.kernelDriverActive(handle, 1);
            if (attached < 0)
            {
                throw new LibUsbException(
                    "Unable to check kernel driver active", result);
            }

            // Detach kernel driver from interface 0 and 1. This can fail if
            // kernel is not attached to the device or operating system
            // doesn't support this operation. These cases are ignored here.
            result = LibUsb.detachKernelDriver(handle, 1);
            if (result != LibUsb.SUCCESS &&
                result != LibUsb.ERROR_NOT_SUPPORTED &&
                result != LibUsb.ERROR_NOT_FOUND)
            {
                throw new LibUsbException("Unable to detach kernel driver",
                    result);
            }

            // Claim interface
            result = LibUsb.claimInterface(handle, 1);
            if (result != LibUsb.SUCCESS)
            {
                throw new LibUsbException("Unable to claim interface", result);
            }


            // Release the interface
            result = LibUsb.releaseInterface(handle, 1);
            if (result != LibUsb.SUCCESS)
            {
                throw new LibUsbException("Unable to release interface", 
                    result);
            }

            // Re-attach kernel driver if needed
            if (attached == 1)
            {
                LibUsb.attachKernelDriver(handle, 1);
                if (result != LibUsb.SUCCESS)
                {
                    throw new LibUsbException(
                        "Unable to re-attach kernel driver", result);
                }
            }

            System.out.println("Exiting");
        }
        finally
        {
            LibUsb.close(handle);
        }

        // Deinitialize the libusb context
        LibUsb.exit(null);
    }
}