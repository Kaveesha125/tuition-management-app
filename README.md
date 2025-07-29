# 📚 Tuition Management Database Schema

> **⚠️ Warning:** This schema is for **context only** and is **not meant to be executed**.  
> The table order and constraints may not be valid for direct execution.

---

```sql
CREATE TABLE public.assignments (
  id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  course_id BIGINT,
  teacher_id BIGINT,
  title VARCHAR,
  description VARCHAR,
  due_date DATE,
  upload_url VARCHAR,
  CONSTRAINT assignments_pkey PRIMARY KEY (id),
  CONSTRAINT assignments_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id),
  CONSTRAINT assignments_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.teachers(teacher_id)
);

CREATE TABLE public.attendance (
  attendance_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  student_id BIGINT,
  teacher_id BIGINT,
  course_id BIGINT,
  date DATE,
  status VARCHAR,
  CONSTRAINT attendance_pkey PRIMARY KEY (attendance_id),
  CONSTRAINT attendance_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.teachers(teacher_id),
  CONSTRAINT attendance_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id),
  CONSTRAINT attendance_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(student_id)
);

CREATE TABLE public.courses (
  course_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  course_name VARCHAR,
  description VARCHAR,
  CONSTRAINT courses_pkey PRIMARY KEY (course_id)
);

CREATE TABLE public.fees (
  fee_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  student_id BIGINT,
  amount NUMERIC,
  month VARCHAR,
  status VARCHAR,
  course_id BIGINT,
  receipt_url DATE,
  CONSTRAINT fees_pkey PRIMARY KEY (fee_id),
  CONSTRAINT fees_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(student_id),
  CONSTRAINT fees_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id)
);

CREATE TABLE public.materials (
  material_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
  uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  course_id BIGINT,
  teacher_id BIGINT,
  title VARCHAR,
  file_url VARCHAR,
  CONSTRAINT materials_pkey PRIMARY KEY (material_id),
  CONSTRAINT materials_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.teachers(teacher_id),
  CONSTRAINT materials_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id)
);

CREATE TABLE public.notifications (
  notification_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
  sent_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  sender_id BIGINT,
  receiver_id BIGINT,
  message VARCHAR,
  type VARCHAR,
  is_read BOOLEAN DEFAULT false,
  CONSTRAINT notifications_pkey PRIMARY KEY (notification_id),
  CONSTRAINT notifications_receiver_id_fkey FOREIGN KEY (receiver_id) REFERENCES public.user(id),
  CONSTRAINT notifications_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.user(id)
);

CREATE TABLE public.students (
  student_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  user_id BIGINT NOT NULL,
  course_id BIGINT,
  parent_contact NUMERIC,
  results DOUBLE PRECISION,
  CONSTRAINT students_pkey PRIMARY KEY (student_id),
  CONSTRAINT Students_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id),
  CONSTRAINT Students_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user(id)
);

CREATE TABLE public.submissions (
  submissions_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  assignment_id BIGINT,
  student_id BIGINT,
  submission_url VARCHAR,
  submitted_at DATE,
  CONSTRAINT submissions_pkey PRIMARY KEY (submissions_id),
  CONSTRAINT submissions_assignment_id_fkey FOREIGN KEY (assignment_id) REFERENCES public.assignments(id),
  CONSTRAINT submissions_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(student_id)
);

CREATE TABLE public.teacher_courses (
  teacher_course_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
  teacher_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT teacher_courses_pkey PRIMARY KEY (teacher_course_id),
  CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES public.courses(course_id),
  CONSTRAINT fk_teacher FOREIGN KEY (teacher_id) REFERENCES public.teachers(teacher_id)
);

CREATE TABLE public.teachers (
  teacher_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  user_id BIGINT,
  CONSTRAINT teachers_pkey PRIMARY KEY (teacher_id),
  CONSTRAINT teachers_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user(id)
);

CREATE TABLE public.user (
  id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  name VARCHAR NOT NULL,
  email VARCHAR NOT NULL UNIQUE,
  password VARCHAR,
  role VARCHAR,
  is_verified BOOLEAN DEFAULT false,
  CONSTRAINT user_pkey PRIMARY KEY (id)
);
