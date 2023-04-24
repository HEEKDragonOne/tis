SELECT
  g.id
, g.entity_id
, s.supplier_ids
, w.warehouse_ids
, gss.self_entity_id_list self_entity_ids
, sic.stock_create_times
, sic.stock_info_warehouse_ids
, g.bar_code
, g.inner_code
, g.name
, g.short_code
, g.spell
, g.standard_goods_id
, g.standard_bar_code
, g.standard_name
, g.unit_type
, g.package_type
, g.category_id
, c.inner_code category_inner_code
, g.type
, '' num_unit_id
, '' num_unit_name
, '' weight_unit_id
, '' weight_unit_name
, '' main_unit_id
, '' main_unit_name
, '' sub_unit_id1
, '' sub_unit_name1
, '' sub_unit_conversion1
, '' sub_unit_id2
, '' sub_unit_name2
, '' sub_unit_conversion2
, '' sub_unit_id3
, '' sub_unit_name3
, '' sub_unit_conversion3
, '' sub_unit_id4
, '' sub_unit_name4
, '' sub_unit_conversion4
, g.specification
, g.server
, g.path
, g.sort_code
, g.period
, g.memo
, g.origin
, '' price_unit_no
, '' inventory_unit_no
, g.percentage
, g.has_degree
, g.is_sales
, g.goods_plate_id
, '' goods_plate_name
, g.is_valid
, g.create_time
, g.op_time
, g.last_ver
, g.extend_fields
, g.apply_time
, g.pt,abs( hash( cast( g.entity_id as string)) % 1 ) AS pmod
FROM
  scmdb.goods g
, tis.supplier_collapse s
, tis.warehouse_collapse w
, scmdb.goods_sync_shop gss
, tis.stock_info_collapse sic
, scmdb.category c
WHERE (((((((((((((g.entity_id IS NOT NULL) AND (g.entity_id = s.entity_id)) AND (g.id = s.goods_id)) AND (g.entity_id = w.self_entity_id)) AND (g.id = w.goods_id)) AND (g.entity_id = gss.entity_id)) AND (gss.is_valid = 1)) AND (g.id = gss.goods_id)) AND (g.entity_id = sic.entity_id)) AND (g.id = sic.goods_id)) AND (g.entity_id = category.entity_id)) AND (g.category_id = c.id)) AND (c.is_valid = 1)) AND g.pt='20200703113848' AND s.pt='20200703113848' AND w.pt='20200703113848' AND gss.pt='20200703113848' AND sic.pt='20200703113848' AND c.pt='20200703113848'
