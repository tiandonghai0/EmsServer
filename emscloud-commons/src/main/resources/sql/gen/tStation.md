sample
===
* 注释

	select #use("cols")# from t_station  where  #use("condition")#

cols
===
	station_id,station_code,city_code,station_name,longitude,latitude,addr,create_date,update_date,create_by,update_by

updateSample
===
	
	station_id=#stationId#,station_code=#stationCode#,city_code=#cityCode#,station_name=#stationName#,longitude=#longitude#,latitude=#latitude#,addr=#addr#,create_date=#createDate#,update_date=#updateDate#,create_by=#createBy#,update_by=#updateBy#

condition
===

	1 = 1  
	@if(!isEmpty(stationId)){
	 and station_id=#stationId#
	@}
	@if(!isEmpty(stationCode)){
	 and station_code=#stationCode#
	@}
	@if(!isEmpty(cityCode)){
	 and city_code=#cityCode#
	@}
	@if(!isEmpty(stationName)){
	 and station_name=#stationName#
	@}
	@if(!isEmpty(longitude)){
	 and longitude=#longitude#
	@}
	@if(!isEmpty(latitude)){
	 and latitude=#latitude#
	@}
	@if(!isEmpty(addr)){
	 and addr=#addr#
	@}
	@if(!isEmpty(createDate)){
	 and create_date=#createDate#
	@}
	@if(!isEmpty(updateDate)){
	 and update_date=#updateDate#
	@}
	@if(!isEmpty(createBy)){
	 and create_by=#createBy#
	@}
	@if(!isEmpty(updateBy)){
	 and update_by=#updateBy#
	@}
	
	