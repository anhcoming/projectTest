package com.viettel.hstd.dto.hstd;

import java.util.ArrayList;
import java.util.List;

public class ProvinceAreaDTO {
    public static class ProvinceAreaRequest {
        public Long id;
        public String code;
        public String name;
        public Long parentId;
        public Boolean status;
        public String path;
        public String nameLevel1;
        public String nameLevel2;
        public String nameLevel3;
        public Long groupOrder;
        public Long groupLevel;
        public Long provinceId;
        public String provinceCode;
        public Long areaId;
        public String areaCode;
        public Long sysGroupId;
        public String sysGroupName;
        public Boolean isActive = true;
       
    }


    public static class ProvinceAreaResponse {
        public Long id;
        public String code;
        public String name;
        public Long parentId;
        public Boolean status;
        public String path;
        public String nameLevel1;
        public String nameLevel2;
        public String nameLevel3;
        public Long groupOrder;
        public Long groupLevel;
        public Long provinceId;
        public String provinceCode;
        public Long areaId;
        public String areaCode;
        public Long sysGroupId;
        public String sysGroupName;

    }

    public static class ProvinceAreaResponseShort {
        public Long id;
        public String code;
        public String name;
        public Long parentId;
    }

    public static class ProvinceAreaTree {
        public Long id;
        public String code;
        public String name;
        public Long parentId;
        public Boolean status;
        public String path;
        public String nameLevel1;
        public String nameLevel2;
        public String nameLevel3;
        public Long groupOrder;
        public Long groupLevel;
        public Long provinceId;
        public String provinceCode;
        public Long areaId;
        public String areaCode;
        public Long sysGroupId;
        public String sysGroupName;
        public List<ProvinceAreaDTO.ProvinceAreaTree> children = new ArrayList<>();

        public void addChild(ProvinceAreaDTO.ProvinceAreaTree categoryTree) {
            if (!this.children.contains(categoryTree) && categoryTree != null) {
                this.children.add(categoryTree);
            }
        }
    }

    public static class ProvinceAreaTreeShort {
        public Long id;
        public String code;
        public String name;
        public Long parentId;
        public List<ProvinceAreaDTO.ProvinceAreaTreeShort> children = new ArrayList<>();

        public void addChild(ProvinceAreaDTO.ProvinceAreaTreeShort categoryTree) {
            if (!this.children.contains(categoryTree) && categoryTree != null) {
                this.children.add(categoryTree);
            }
        }
    }
}
