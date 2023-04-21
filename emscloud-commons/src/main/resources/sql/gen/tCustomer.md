sample
===
* 注释

	select #use("cols")# from t_customer  where  #use("condition")#

cols
===
	customer_id,customer_name,contact_name,email,addr,tel,mobile,create_date,update_date,create_by,update_by

updateSample
===
	
	customer_id=#customerId#,customer_name=#customerName#,contact_name=#contactName#,email=#email#,addr=#addr#,tel=#tel#,mobile=#mobile#,create_date=#createDate#,update_date=#updateDate#,create_by=#createBy#,update_by=#updateBy#

condition
===

	1 = 1  
	@if(!isEmpty(customerId)){
	 and customer_id=#customerId#
	@}
	@if(!isEmpty(customerName)){
	 and customer_name=#customerName#
	@}
	@if(!isEmpty(contactName)){
	 and contact_name=#contactName#
	@}
	@if(!isEmpty(email)){
	 and email=#email#
	@}
	@if(!isEmpty(addr)){
	 and addr=#addr#
	@}
	@if(!isEmpty(tel)){
	 and tel=#tel#
	@}
	@if(!isEmpty(mobile)){
	 and mobile=#mobile#
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
	
	