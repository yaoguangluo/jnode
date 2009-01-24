/*
 * $Id$
 */
package org.jnode.emu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.jnode.driver.AbstractDeviceManager;
import org.jnode.driver.Device;
import org.jnode.driver.DeviceFinder;
import org.jnode.driver.DeviceToDriverMapper;
import org.jnode.driver.DriverException;

/**
 * @author Levente S\u00e1ntha
 */
public class DeviceManager extends AbstractDeviceManager {
    public static final Logger log = Logger.getLogger(DeviceManager.class);

    public static final DeviceManager INSTANCE = new DeviceManager();

    private List<DeviceFinder> finders = new ArrayList<DeviceFinder>();

    private List<DeviceToDriverMapper> mappers = new ArrayList<DeviceToDriverMapper>();

    private DeviceManager() {
    }

    public void removeAll() {
        finders.clear();
        mappers.clear();

        for (Device device : getDevices()) {
            try {
                unregister(device);
            } catch (DriverException e) {
                log.error("can't unregister " + device);
            }
        }
    }

    public void add(DeviceFinder finder, DeviceToDriverMapper mapper) {
        boolean doStart = false;

        if (!finders.contains(finder)) {
            finders.add(finder);
            doStart = true;
        }

        if (!mappers.contains(mapper)) {
            mappers.add(mapper);
            doStart = true;
        }

        if (doStart) {
            start();
        }
    }

    /**
     * Start this manager
     */
    public final void start() {
        // Thread thread = new Thread()
        // {
        // public void run()
        // {
        log.debug("Loading extensions ...");
        loadExtensions();
        log.debug("Extensions loaded !");
        // }
        // };
        // thread.start();

        try {
            // must be called before findDeviceDrivers
            log.debug("findDevices ...");
            findDevices();

            log.debug("findDeviceDrivers ...");
            findDeviceDrivers();

            log.debug("StubDeviceManager initialized !");
        } catch (InterruptedException e) {
            log.fatal("can't find devices", e);
        }
    }

    protected final void refreshFinders(List<DeviceFinder> finders) {
        log.info("refreshFinders");
        finders.clear();
        finders.addAll(this.finders);
    }

    protected final void refreshMappers(List<DeviceToDriverMapper> mappers) {
        log.info("refreshMappers");
        mappers.clear();
        mappers.addAll(this.mappers);

        // Now sort them
        Collections.sort(mappers, MapperComparator.INSTANCE);
    }
}