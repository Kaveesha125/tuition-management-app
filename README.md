-- WARNING: This schema is for context only and is not meant to be run.
-- Table order and constraints may not be valid for execution.

CREATE TABLE public.assignments (
  id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  course_id bigint,
  teacher_id bigint,
  title character varying,
  description character varying,
  due_date date,
  upload_url character varying,
  CONSTRAINT assignments_pkey PRIMARY KEY (id),
  CONSTRAINT assignments_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id),
  CONSTRAINT assignments_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.teachers(teacher_id)
);
CREATE TABLE public.attendance (
  attendance_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  student_id bigint,
  teacher_id bigint,
  course_id bigint,
  date date,
  status character varying,
  CONSTRAINT attendance_pkey PRIMARY KEY (attendance_id),
  CONSTRAINT attendance_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.teachers(teacher_id),
  CONSTRAINT attendance_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id),
  CONSTRAINT attendance_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(student_id)
);
CREATE TABLE public.courses (
  course_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  course_name character varying,
  description character varying,
  CONSTRAINT courses_pkey PRIMARY KEY (course_id)
);
CREATE TABLE public.fees (
  fee_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  student_id bigint,
  amount numeric,
  month character varying,
  status character varying,
  course_id bigint,
  receipt_url date,
  CONSTRAINT fees_pkey PRIMARY KEY (fee_id),
  CONSTRAINT fees_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(student_id),
  CONSTRAINT fees_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id)
);
CREATE TABLE public.materials (
  material_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  uploaded_at timestamp with time zone NOT NULL DEFAULT now(),
  course_id bigint,
  teacher_id bigint,
  title character varying,
  file_url character varying,
  CONSTRAINT materials_pkey PRIMARY KEY (material_id),
  CONSTRAINT materials_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.teachers(teacher_id),
  CONSTRAINT materials_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id)
);
CREATE TABLE public.notifications (
  notification_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  sent_at timestamp with time zone NOT NULL DEFAULT now(),
  sender_id bigint,
  receiver_id bigint,
  message character varying,
  type character varying,
  is_read boolean DEFAULT false,
  CONSTRAINT notifications_pkey PRIMARY KEY (notification_id),
  CONSTRAINT notifications_receiver_id_fkey FOREIGN KEY (receiver_id) REFERENCES public.user(id),
  CONSTRAINT notifications_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.user(id)
);
CREATE TABLE public.students (
  student_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  user_id bigint NOT NULL,
  course_id bigint,
  parent_contact numeric,
  results double precision,
  CONSTRAINT students_pkey PRIMARY KEY (student_id),
  CONSTRAINT Students_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id),
  CONSTRAINT Students_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user(id)
);
CREATE TABLE public.submissions (
  submissions_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  assignment_id bigint,
  student_id bigint,
  submission_url character varying,
  submitted_at date,
  CONSTRAINT submissions_pkey PRIMARY KEY (submissions_id),
  CONSTRAINT submissions_assignment_id_fkey FOREIGN KEY (assignment_id) REFERENCES public.assignments(id),
  CONSTRAINT submissions_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(student_id)
);
CREATE TABLE public.teacher_courses (
  teacher_course_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  teacher_id bigint NOT NULL,
  course_id bigint NOT NULL,
  assigned_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT teacher_courses_pkey PRIMARY KEY (teacher_course_id),
  CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES public.courses(course_id),
  CONSTRAINT fk_teacher FOREIGN KEY (teacher_id) REFERENCES public.teachers(teacher_id)
);
CREATE TABLE public.teachers (
  teacher_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  user_id bigint,
  CONSTRAINT teachers_pkey PRIMARY KEY (teacher_id),
  CONSTRAINT teachers_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user(id)
);
CREATE TABLE public.user (
  id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  name character varying NOT NULL,
  email character varying NOT NULL UNIQUE,
  password character varying,
  role character varying,
  is_verified boolean DEFAULT false,
  CONSTRAINT user_pkey PRIMARY KEY (id)
);
