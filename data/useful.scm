(define list (lambda (first . rest)
               (_list first rest)))
(define _list (lambda (first  rest)
               (if (null? rest)
                   (cons first '())
                   (cons first (_list (car rest) (cdr rest))))))
(define rec
  (lambda (combine id lst)
    (println (list 'rec combine id lst))
    (println (list 'lst lst 'null? (null? lst)))
    ;(println (car lst))
    ;(println (cdr lst))
    (if (null? lst)
        (begin
          (println (list 'id id))
          id
        )
        (begin
          (println (list 'lst lst 'cdr (car lst)))
          (combine (car lst) (rec combine id (cdr lst))))
        )
  )
)

(define sum
  (lambda (start . rest)
    (+ start (rec + 0 rest))))
