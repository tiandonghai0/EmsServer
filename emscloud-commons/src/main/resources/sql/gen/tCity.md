sample
===
* 注释

	select #use("cols")# from t_city  where  #use("condition")#

cols
===
	city_code,province_code,city_name,city_level

updateSample
===
	
	city_code=#cityCode#,province_code=#provinceCode#,city_name=#cityName#,city_level=#cityLevel#

condition
===

	1 = 1  
	@if(!isEmpty(cityCode)){
	 and city_code=#cityCode#
	@}
	@if(!isEmpty(provinceCode)){
	 and province_code=#provinceCode#
	@}
	@if(!isEmpty(cityName)){
	 and city_name=#cityName#
	@}
	@if(!isEmpty(cityLevel)){
	 and city_level=#cityLevel#
	@}
	
	