sample
===
* 注释

	select #use("cols")# from t_user  where  #use("condition")#

cols
===
	account,password,name,email,create_date,update_date

updateSample
===
	
	account=#account#,password=#password#,name=#name#,email=#email#,create_date=#createDate#,update_date=#updateDate#

condition
===

	1 = 1  
	@if(!isEmpty(account)){
	 and account=#account#
	@}
	@if(!isEmpty(password)){
	 and password=#password#
	@}
	@if(!isEmpty(name)){
	 and name=#name#
	@}
	@if(!isEmpty(email)){
	 and email=#email#
	@}
	@if(!isEmpty(createDate)){
	 and create_date=#createDate#
	@}
	@if(!isEmpty(updateDate)){
	 and update_date=#updateDate#
	@}
	
	