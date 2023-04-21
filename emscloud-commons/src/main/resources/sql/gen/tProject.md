sample
===
* 注释

	select #use("cols")# from t_project  where  #use("condition")#

cols
===
	project_id,customer_id,project_name,create_date,update_date,create_by,update_by

updateSample
===
	
	project_id=#projectId#,customer_id=#customerId#,project_name=#projectName#,create_date=#createDate#,update_date=#updateDate#,create_by=#createBy#,update_by=#updateBy#

condition
===

	1 = 1  
	@if(!isEmpty(projectId)){
	 and project_id=#projectId#
	@}
	@if(!isEmpty(customerId)){
	 and customer_id=#customerId#
	@}
	@if(!isEmpty(projectName)){
	 and project_name=#projectName#
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
	
	