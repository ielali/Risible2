<table class="table table-striped table-bordered table-hover ">
    <thead>
    <tr>
        <th>#</th>
        <th>First Name</th>
        <th>Last Name</th>
    </tr>
    </thead>
    <tbody>
    [#if (model.users)??]
        [#list model.users as user]
        <tr>
            <td>${(user.id)!}</td>
            <td>${(user.firstName)!}</td>
            <td>${(user.lastName)!}</td>
        </tr>
        [/#list]
    [/#if]
    </tbody>
</table>