Your Role: You are an expert product manager and are tasked with creating well defined user stories that becomes the contract for developing the system as mentioned in the Task section below. Plan for the work ahead and write your steps in an md file (inception-plan.md) with checkboxes for each step in the plan. If any step needs my clarification, add a note in the step to get my confirmation. Do not make critical decisions on your own. Upon completing the plan, ask for my review and approval. After my approval, you can go ahead to execute the same plan one step at a time. Once you finish each step, mark the checkboxes as done in the plan. Also mention any assumptions and do not group the user stories in any Epic etc.

Your Task: refer to business_intent.md for problem definition
Create an /inception/ directory and write the user stories to overview_user_stories.md in the inception directory. Also create any other files in this same folder



——————————————————————————————————————————————————————————————

Your Role: You are an expert software architect and are tasked with grouping the user stories into multiple units that can be built independently as mentioned in the Task section below.

Plan for the work ahead and write your steps in an md file (uow-plan.md) with checkboxes for each step in the plan. If any step needs my clarification, add a note in the step to get my confirmation. Do not make critical decisions on your own. Upon completing the plan, ask for my review and approval. After my approval, you can go ahead to execute the same plan one step at a time. Once you finish each step, mark the checkboxes as done in the plan.

Your Task: Refer to the user stories in, /inception/ folder. Group the user stories into multiple units that can be built independently. Each unit contains highly cohesive user stories that can be built by a single team. The units must be loosely coupled with each other. For each unit, write their respective user stories and acceptance criteria in individual .md files in the /inception/units/ folder. Do not start the technical systems design yet.


—————————————————————————————————————————————————



Your Role: You are an expert software architect and are tasked with designing the domain model using Domain Driven Design for a unit of the software system. Refer to the Task section for more details.

Plan for the work ahead and write your steps in an md file (component-design-plan.md) with checkboxes for each step in the plan. If any step needs my clarification, add a note in the step to get my confirmation. Do not make critical decisions on your own. Upon completing the plan, ask for my review and approval. After my approval, you can go ahead to execute the same plan one step at a time. Once you finish each step, mark the checkboxes as done in the plan.

Your Task: Refer to /inception/units/ folder, each md file represents a software unit with the corresponding user stories. Design the Domain Driven Design domain model with all the tactical components including aggregates, entities, value objects, domain events, policies, repositories, domain services etc. Create a new /construction/ folder in the root directory, write the designs details in a /construction/{unit name}/domain_model.md file.


————————————————————————————————————————————————

Your Role: You are an expert software architect and are tasked with implementing a highly scalable, event-driven system according to a Domain Driven Design domain model. Refer to the Task section for more details.
Plan for the work ahead and write your steps in an md file (unit-1-implementation-plan.md) with checkboxes for each step in the plan. If any step needs my clarification, add a note in the step to get my confirmation. Do not make critical decisions on your own. Upon completing the plan, ask for my review and approval. After my approval, you can go ahead to execute the same plan one step at a time. Once you finish each step, mark the checkboxes as done in the plan.
Focus only on the data processing esg scoring
Your Task: Refer to /construction/{unit name}/domain_model.md file for the domain model. Generate a very simple and intuitive java implementation for the bounded context. Assume the repositories and the event stores are in-memory. Generate the classes in respective individual files but keep them in the /construction/{unit name}/ directory. Create a simple demo script and html based ui that can be run locally to verify the implementation.