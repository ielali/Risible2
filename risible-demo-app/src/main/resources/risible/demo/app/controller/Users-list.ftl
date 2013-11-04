<!-- Tables
      ================================================== -->
<div class="bs-docs-section">
    <div class="row">
        <div class="col-lg-12">
            <div class="page-header">
                <h1 id="users">Users</h1>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-lg-8">
            <div id="usersDiv" class="bs-example table-responsive">
                [#include "./usersTable.ftl"/]
            </div>
        </div>
        <div class="col-lg-4">
            <div class="well">
                <form id="newUserFrom" class="bs-example form-horizontal" action="addUser.ajax" method="post">
                    <fieldset>
                        <legend>New</legend>
                        <input type="hidden" name="user" value="${users?size}">
                        <div class="form-group">
                            <div class="col-lg-10">
                                <input type="text" class="form-control" id="inputFirstName" placeholder="First Name"
                                       name="user.firstName">
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-lg-10">
                                <input type="text" class="form-control" id="inputLastName" placeholder="Last Name"
                                       name="user.lastName">
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-lg-5">
                                <button id="addButton" type="button" class="btn btn-primary">Add</button>
                            </div>
                        </div>
                    </fieldset>
                </form>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    $('#addButton').on('click',function(){
        var frm = $('#newUserFrom');
        $.ajax({
            type: frm.attr('method'),
            url: frm.attr('action'),
            data: frm.serialize(),
            success: function (data) {
                $('#usersDiv').html(data);
            }
        });
    });
</script>