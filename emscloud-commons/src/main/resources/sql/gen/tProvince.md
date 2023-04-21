sample
===
* 注释

	select #use("cols")# from t_province  where  #use("condition")#

cols
===
	province_code,province_name,region_code,regin_name

updateSample
===
	
	province_code=#provinceCode#,province_name=#provinceName#,region_code=#regionCode#,regin_name=#reginName#

condition
===

	1 = 1  
	@if(!isEmpty(provinceCode)){
	 and province_code=#provinceCode#
	@}
	@if(!isEmpty(provinceName)){
	 and province_name=#provinceName#
	@}
	@if(!isEmpty(regionCode)){
	 and region_code=#regionCode#
	@}
	@if(!isEmpty(reginName)){
	 and regin_name=#reginName#
	@}
	
	