sample
===
* 注释

	select #use("cols")# from t_electric_timeslot  where  #use("condition")#

cols
===
	electric_timeslot_id,electric_price_id,timeslot_type,start_time,end_time,create_date,update_date,create_by,update_by

updateSample
===
	
	electric_timeslot_id=#electricTimeslotId#,electric_price_id=#electricPriceId#,timeslot_type=#timeslotType#,start_time=#startTime#,end_time=#endTime#,create_date=#createDate#,update_date=#updateDate#,create_by=#createBy#,update_by=#updateBy#

condition
===

	1 = 1  
	@if(!isEmpty(electricTimeslotId)){
	 and electric_timeslot_id=#electricTimeslotId#
	@}
	@if(!isEmpty(electricPriceId)){
	 and electric_price_id=#electricPriceId#
	@}
	@if(!isEmpty(timeslotType)){
	 and timeslot_type=#timeslotType#
	@}
	@if(!isEmpty(startTime)){
	 and start_time=#startTime#
	@}
	@if(!isEmpty(endTime)){
	 and end_time=#endTime#
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
	
	