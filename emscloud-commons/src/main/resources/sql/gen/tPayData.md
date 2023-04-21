sample
===
* 注释

	select #use("cols")# from t_pay_data  where  #use("condition")#

cols
===
	id,pay_num,pay_period,hirer_id,create_date,update_date,create_by,update_by

updateSample
===
	
	id=#id#,pay_num=#payNum#,pay_period=#payPeriod#,hirer_id=#hirerId#,create_date=#createDate#,update_date=#updateDate#,create_by=#createBy#,update_by=#updateBy#

condition
===

	1 = 1  
	@if(!isEmpty(id)){
	 and id=#id#
	@}
	@if(!isEmpty(payNum)){
	 and pay_num=#payNum#
	@}
	@if(!isEmpty(payPeriod)){
	 and pay_period=#payPeriod#
	@}
	@if(!isEmpty(hirerId)){
	 and hirer_id=#hirerId#
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
	
	