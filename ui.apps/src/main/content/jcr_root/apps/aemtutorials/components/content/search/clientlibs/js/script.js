$(function () {
    $('#fulltext-search').submit(function (event) {
        event.preventDefault();

        var formEl = $(this);
        var submitButton = $('input[type=submit]', formEl);

        $.ajax({
            type: 'POST',
            url: formEl.prop('action'),
            accept: {
                javascript: 'application/javascript'
            },
            data: {
                'keyword': $('#keyword').val()
            },
            beforeSend: function () {
                submitButton.prop('disabled', 'disabled');
            }
        }).done(function (data) {
            submitButton.prop('disabled', false);
        });
    });
});