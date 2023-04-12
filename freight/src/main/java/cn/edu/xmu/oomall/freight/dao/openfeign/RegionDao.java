package cn.edu.xmu.oomall.freight.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.service.dto.SimpleRegionDto;
import cn.edu.xmu.oomall.freight.dao.bo.State;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("region-service")
public interface RegionDao {
    @GetMapping("/region/states")
    InternalReturnObject<List<State>> getRegionStates();

  /*  @PostMapping("/shops/{did}/regions/{id}/subregions")
    InternalReturnObject<SimpleRegion> createSubregion(@PathVariable Long did, @PathVariable Long id);*/

    @GetMapping("/shops/{did}/regions/{id}/subregions")
    InternalReturnObject<PageDto<SimpleRegionDto>> getSubregionByShopIdAndRegionId(@PathVariable Long did, @PathVariable Long id,
                                                                                   @RequestParam(defaultValue = "1") Integer page,
                                                                                   @RequestParam(defaultValue = "10") Integer pageSize
                                                                             );

    @GetMapping("/regions/{id}/subregions")
    InternalReturnObject<PageDto<SimpleRegionDto>> getSubregionByRegionId(@PathVariable Long id,
                                                                          @RequestParam(defaultValue = "1") Integer page,
                                                                          @RequestParam(defaultValue = "10") Integer pageSize
                                                                       );


    @GetMapping("/regions/{id}")
    InternalReturnObject<Region> getRegionById(@PathVariable Long id);

    @GetMapping("/internal/regions/{id}/parents")
    InternalReturnObject<List<SimpleRegionDto>> getParentsByRegionId(@PathVariable Long id);
}
