/*
 * $Id$
 *
 * Copyright (C) 2003-2014 JNode.org
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; If not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
 
package org.jnode.test.fs.hfsplus;

import java.io.File;
import java.io.IOException;
import org.jnode.driver.Device;
import org.jnode.driver.block.FileDevice;
import org.jnode.fs.hfsplus.HFSPlusParams;
import org.jnode.fs.hfsplus.HfsPlusFileSystem;
import org.jnode.fs.hfsplus.HfsPlusFileSystemType;
import org.jnode.fs.hfsplus.SuperBlock;
import org.jnode.test.fs.DataStructureAsserts;
import org.jnode.fs.FSDirectory;
import org.jnode.test.fs.FileSystemTestUtils;
import org.jnode.fs.service.FileSystemService;
import org.jnode.test.support.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HfsPlusFileSystemTest {

    private Device device;
    private FileSystemService fss;

    @Before
    public void setUp() throws Exception {
        // create test device.
        device = createTestDisk(false);
        // create file system service.
        fss = FileSystemTestUtils.createFSService(HfsPlusFileSystemType.class.getName());
    }

    @Test
    public void testReadSmallDisk() throws Exception {

        device = new FileDevice(FileSystemTestUtils.getTestFile("test/fs/hfsplus/test.hfsplus"), "r");
        HfsPlusFileSystemType type = fss.getFileSystemType(HfsPlusFileSystemType.ID);
        HfsPlusFileSystem fs = type.create(device, true);

        String expectedStructure =
            "type: HFS+ vol:Kenny total:67108864 free:66035712\n" +
            "  /; \n" +
            "    southpark.jpeg; 6420; 5a2ec290089ee04a470135f3bda29f94\n" +
            "    test.txt; 1141; 48b97c1f1defb52c77ce75d55a4b066c\n" +
            "    \u0000\u0000\u0000\u0000HFS+ Private Data; \n";

        DataStructureAsserts.assertStructure(fs, expectedStructure);
    }

    @Test
    public void testReadDiskWithDirectoryHardLinks() throws Exception {

        device = new FileDevice(FileSystemTestUtils.getTestFile("test/fs/hfsplus/hard-linked-directories.dmg"), "r");
        HfsPlusFileSystemType type = fss.getFileSystemType(HfsPlusFileSystemType.ID);
        HfsPlusFileSystem fs = type.create(device, true);

        String expectedStructure =
            "type: HFS+ vol:Hard linked directories total:40960000 free:39428096\n" +
            "  /; \n" +
            "    .DS_Store; 6148; cbdca44c18b8de8671b413b2023ef664\n" +
            "    .fseventsd; \n" +
            "      00000000214ea109; 231; 11618d6f301d3672e498609838d23a8c\n" +
            "      00000000214ea10a; 72; 6f20b722869a82510ca98e99071c4aca\n" +
            "      fseventsd-uuid; 36; 2b95938d530cb32e96dfc01671095522\n" +
            "    .HFS+ Private Directory Data\r; \n" +
            "      dir_25; \n" +
            "        file.txt; 38; 23c1bd7263b9abbdbb879e6267d84ff8\n" +
            "    .journal; 524288; 7c1d0a50a9738dd88572a9cee56c0270\n" +
            "    .journal_info_block; 4096; 469270564228a832e83d2ad16e6d8edc\n" +
            "    .Trashes; \n" +
            "    dir1; \n" +
            "      clone; \n" +
            "        file.txt; 38; 23c1bd7263b9abbdbb879e6267d84ff8\n" +
            "    dir2; \n" +
            "      clone; \n" +
            "        file.txt; 38; 23c1bd7263b9abbdbb879e6267d84ff8\n" +
            "    \u0000\u0000\u0000\u0000HFS+ Private Data; \n";

        DataStructureAsserts.assertStructure(fs, expectedStructure);
    }

    @Test
    public void testReadDiskWithFileHardLinks() throws Exception {

        device = new FileDevice(FileSystemTestUtils.getTestFile("test/fs/hfsplus/hard-linked-files.dmg"), "r");
        HfsPlusFileSystemType type = fss.getFileSystemType(HfsPlusFileSystemType.ID);
        HfsPlusFileSystem fs = type.create(device, true);

        String expectedStructure =
            "type: HFS+ vol:hard-links total:40960000 free:39436288\n" +
                "  /; \n" +
                "    .DS_Store; 6148; b5ae7323596898677123c65fcce1be07\n" +
                "    .fseventsd; \n" +
                "      00000000214eb5f7; 121; 45f770b87c4fb7773466ed4ea7333248\n" +
                "      00000000214eb5f8; 72; 261dba091a629e61f127ed183b42ae01\n" +
                "      fseventsd-uuid; 36; f4f9aca6866b93ba4ab04768132dbbf6\n" +
                "    .HFS+ Private Directory Data\r; \n" +
                "    .journal; 524288; 7d69775e76f5a59e0f8687f792df23dc\n" +
                "    .journal_info_block; 4096; 469270564228a832e83d2ad16e6d8edc\n" +
                "    .Trashes; \n" +
                "    arrest.txt; 1933; bedea6f1277f61a924388fbb58281e4a\n" +
                "    diapers.txt; 1933; bedea6f1277f61a924388fbb58281e4a\n" +
                "    \u0000\u0000\u0000\u0000HFS+ Private Data; \n" +
                "      iNode27; 1933; bedea6f1277f61a924388fbb58281e4a\n";

        DataStructureAsserts.assertStructure(fs, expectedStructure);
    }

    @Test
    public void testCreate() throws Exception {
        HfsPlusFileSystemType type = fss.getFileSystemType(HfsPlusFileSystemType.ID);
        HfsPlusFileSystem fs = new HfsPlusFileSystem(device, false, type);
        HFSPlusParams params = new HFSPlusParams();
        params.setVolumeName("testdrive");
        params.setBlockSize(HFSPlusParams.OPTIMAL_BLOCK_SIZE);
        params.setJournaled(false);
        params.setJournalSize(HFSPlusParams.DEFAULT_JOURNAL_SIZE);
        fs.create(params);
        SuperBlock vh = fs.getVolumeHeader();
        Assert.assertEquals(SuperBlock.HFSPLUS_SUPER_MAGIC, vh.getMagic());
        Assert.assertEquals(4096, vh.getBlockSize());

    }

    @Test
    public void testRead() throws Exception {
        HfsPlusFileSystemType type = fss.getFileSystemType(HfsPlusFileSystemType.ID);
        HfsPlusFileSystem fs = new HfsPlusFileSystem(device, false, type);
        HFSPlusParams params = new HFSPlusParams();
        params.setVolumeName("testdrive");
        params.setBlockSize(HFSPlusParams.OPTIMAL_BLOCK_SIZE);
        params.setJournaled(false);
        params.setJournalSize(HFSPlusParams.DEFAULT_JOURNAL_SIZE);
        fs.create(params);
        fs.close();
        fs = new HfsPlusFileSystemType().create(device, false);
        fs.read();
        fs.createRootEntry();
        FSDirectory root = fs.getRootEntry().getDirectory();
        Assert.assertFalse("Must be empty", root.iterator().hasNext());
        root.addDirectory("test");
        fs.flush();
        fs.close();
        fs = new HfsPlusFileSystemType().create(device, false);
        fs.read();
        Assert.assertEquals(1, fs.getVolumeHeader().getFolderCount());
        fs.createRootEntry();
        root = fs.getRootEntry().getDirectory();
        Assert.assertTrue("Must contains one directory", root.iterator().hasNext());
    }

    private Device createTestDisk(boolean formatted) throws IOException {
        File file = TestUtils.makeTempFile("hfsDevice", "10M");
        Device device = new FileDevice(file, "rw");
        return device;

    }
}
