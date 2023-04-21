sample
===
* 注释

	select #use("cols")# from t_electric_price  where  #use("condition")#

cols
===
	electric_price_id,peak_price,valley_price,flat_price,remarks,create_date,update_date,create_by,update_by

updateSample
===
	
	electric_price_id=#electricPriceId#,peak_price=#peakPrice#,valley_price=#valleyPrice#,flat_price=#flatPrice#,remarks=#remarks#,create_date=#createDate#,update_date=#updateDate#,create_by=#createBy#,update_by=#updateBy#

condition
===

	1 = 1  
	@if(!isEmpty(electricPriceId)){
	 and electric_price_id=#electricPriceId#
	@}
	@if(!isEmpty(peakPrice)){
	 and peak_price=#peakPrice#
	@}
	@if(!isEmpty(valleyPrice)){
	 and valley_price=#valleyPrice#
	@}
	@if(!isEmpty(flatPrice)){
	 and flat_price=#flatPrice#
	@}
	@if(!isEmpty(remarks)){
	 and remarks=#remarks#
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
	
	