select
===
* 注释

	select #use("cols")# from t_user  where  #use("condition")#

cols
===
	account,password,name,email

update
===
	
	account=#account#,password=#password#,name=#name#,email=#email#

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
	
	