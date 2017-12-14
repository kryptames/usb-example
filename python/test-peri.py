from practicum import find_mcu_boards
from peri import McuWithPeriBoard
from time import sleep

devs = find_mcu_boards()

if len(devs) == 0:
    print("*** No practicum board found.")
    exit(1)

b = McuWithPeriBoard(devs[0])
print("*** Practicum board found")
print("*** Manufacturer: %s" % \
        b.handle.getString(b.device.iManufacturer, 256))
print("*** Product: %s" % \
        b.handle.getString(b.device.iProduct, 256))

count = 0
while True:
    b.setLedValue(count)
    sw = b.getSwitch()
    light = b.getLight()

    if sw is True:
        state = "PRESSED"
    else:
        state = "RELEASED"

    print("LEDs set to %d | Switch state: %-8s | Light value: %d" % (
            count, state, light))

    count = (count + 1) % 8
    sleep(0.5)

