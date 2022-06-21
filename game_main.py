import math
import time
import pygame
import sys

#defining global variables/tuples here:
G = 6.67408*(10**-11)

BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
GREY = (200, 200, 200)
RED = (255, 000, 000)
BLUE = (000, 000, 255)
GREEN = (000, 255, 000)
ORANGE = (255, 165, 000)


class Mass:

    def __init__(self, name, mass, radius, parent_planet, orbital_radius, true_anomaly=0, orbital_velo=None):
        #characteristics about the planetary mass
        self.name = name
        self.mass = mass
        self.radius = radius
        self.parent_planet = parent_planet #planet which the mass orbits around
        self.orbital_radius = orbital_radius #initial distance to the parent planet
        self.true_anomaly = true_anomaly #initial placement of the planet using polar coordinates
        self.lead_positions = []
        self.lead_velocities = []
        
        if self.parent_planet != None:
            #circular orbit assumed if no initial tangential velocity is provided
            if orbital_velo == None:
                orbital_velo = -1*math.sqrt((G*self.parent_planet.mass)/(1000*self.orbital_radius)) / 1000

            self.xvelo = orbital_velo*math.cos(self.true_anomaly+math.pi/2)
            self.yvelo = orbital_velo*math.sin(self.true_anomaly+math.pi/2)
        else:
            self.xpos, self.ypos = 0, 0
            self.xvelo, self.yvelo = 0, 0


    def __repr__(self):
        return self.name
    
    def gravitational_influence(self, time_step):
        #vectors reffered in [magnitude, angle] pairs
        #all angles drawn from the mass
        gravity_vectors = []

        for planet in planet_list:
            if planet == self:
                continue
            deltax = self.xpos - planet.xpos
            deltay = self.ypos - planet.ypos
            distance = math.sqrt((deltax**2) + (deltay**2))

            #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
            force_gravity = ((G*self.mass*planet.mass) / (1000*distance)**2) / 1000
            theta = math.atan2(deltay, deltax)

            gravity_vectors.append((force_gravity, theta))

        net_xforce, net_yforce = 0, 0
        for vector in gravity_vectors:
            
            net_xforce += vector[0] * -math.cos(vector[1])
            net_yforce += vector[0] * -math.sin(vector[1])

        net_xaccel = net_xforce / self.mass
        net_yaccel = net_yforce / self.mass

        #calculates the change in velocity over a given time step interval
        self.xvelo += net_xaccel * time_step
        self.yvelo += net_yaccel * time_step	

    def deltaPosition(self, time_step):
        #determines the change in position over the same time step interval
        self.xpos += self.xvelo * time_step
        self.ypos += self.yvelo * time_step


class SpaceCraft:

    def __init__(self, name, mass, length, initial_parent, orbital_radius, true_anomaly=0, orbital_velo=None):
        self.name = name
        self.mass = mass
        self.length = length
        self.initial_parent = initial_parent
        self.orbital_radius = orbital_radius #initial distance to the parent planet
        self.true_anomaly = true_anomaly #initial placement of the planet using polar coordinates
        #circular orbit assumed if no initial tangential velocity is provided
        if orbital_velo == None:
            orbital_velo = -1*math.sqrt((G*self.initial_parent.mass)/(1000*self.orbital_radius)) / 1000 #convert m/s to km/s

        self.xvelo = orbital_velo*math.cos(self.true_anomaly+math.pi/2)
        self.yvelo = orbital_velo*math.sin(self.true_anomaly+math.pi/2)
        self.xpos, self.ypos = 0, 0
        self.fuel_remaining = 1000


    def __repr__(self):
        return self.name

    def gravitational_influence(self, time_step):
        #vectors reffered in [magnitude, angle] pairs
        #all angles drawn from the space craft
        gravity_vectors = []

        for planet in planet_list:
            deltax = self.xpos - planet.xpos
            deltay = self.ypos - planet.ypos
            distance = math.sqrt((deltax**2) + (deltay**2))

            #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
            force_gravity = ((G*self.mass*planet.mass) / (1000*distance)**2) / 1000
            theta = math.atan2(deltay, deltax)

            gravity_vectors.append((force_gravity, theta))

        net_xforce, net_yforce = 0, 0
        for vector in gravity_vectors:
            
            net_xforce += vector[0] * -math.cos(vector[1])
            net_yforce += vector[0] * -math.sin(vector[1])

        net_xaccel = net_xforce / self.mass
        net_yaccel = net_yforce / self.mass

        #calculates the change in velocity over a given time step interval
        self.xvelo += net_xaccel * time_step
        self.yvelo += net_yaccel * time_step	

    def deltaPosition(self, time_step):
        #determines the change in position over the same time step interval
        self.xpos += self.xvelo * time_step
        self.ypos += self.yvelo * time_step
       

    def drawCraft(self, screen, position, SCALE_FACTOR):
        ### input display x-y positions along with a scale factor
        #calculates the measurements of the craft given its total length
        xpos, ypos = position[0], position[1]
        tank_length = (1/(SCALE_FACTOR/200)) * self.length * (2/3)
        tank_width = tank_length / 3
        tank_diagonal = math.sqrt(tank_length**2 + tank_width**2)
        tail_diagonal = math.sqrt((tank_length)**2 + (2*tank_width)**2)
        
        #determines the direction of the craft based on its velocity
        nose_direction = math.atan2(self.yvelo, self.xvelo)
        nose_pos = (xpos + math.cos(nose_direction)*(tank_length*2), ypos + math.sin(nose_direction)*(tank_length*2))

        #p1-4: four edges of the tank in counterclockwise order starting from top right (when the craft is pointed up)
        p1_angle = nose_direction - math.atan2(tank_width, tank_length)
        p2_angle = nose_direction + math.atan2(tank_width, tank_length)
        p3_angle = p1_angle + math.pi
        p4_angle = p2_angle - math.pi
        
        #craft is drawn relative using polar coordinates relative to its display center
        p1_pos = (xpos + math.cos(p1_angle)*(tank_diagonal), ypos + math.sin(p1_angle)*(tank_diagonal))
        p2_pos = (xpos + math.cos(p2_angle)*(tank_diagonal), ypos + math.sin(p2_angle)*(tank_diagonal))
        p3_pos = (xpos + math.cos(p3_angle)*(tank_diagonal), ypos + math.sin(p3_angle)*(tank_diagonal))
        p4_pos = (xpos + math.cos(p4_angle)*(tank_diagonal), ypos + math.sin(p4_angle)*(tank_diagonal))

        #all points are collected 
        tank_points = (p1_pos, p2_pos, p3_pos, p4_pos)
        nose_points = (p1_pos, p2_pos, nose_pos)
        right_tail_points = (
            (xpos + math.cos(nose_direction - math.pi/2)*(tank_width), ypos + math.sin(nose_direction - math.pi/2)*(tank_width)), 
            (xpos + math.cos(nose_direction - 2.5)*(tail_diagonal), ypos + math.sin(nose_direction - 2.5)*(tail_diagonal)), 
            p4_pos
        )
        left_tail_points = (
            (xpos + math.cos(nose_direction + math.pi/2)*(tank_width), ypos + math.sin(nose_direction + math.pi/2)*(tank_width)), 
            (xpos + math.cos(nose_direction + 2.5)*(tail_diagonal), ypos + math.sin(nose_direction + 2.5)*(tail_diagonal)), 
            p3_pos
        )
        #each part of the craft is drawn as separate polygons
        pygame.draw.polygon(screen, RED, nose_points)
        pygame.draw.polygon(screen, RED, right_tail_points)
        pygame.draw.polygon(screen, RED, left_tail_points)
        pygame.draw.polygon(screen, GREY, tank_points)
        #pygame.draw.line(screen, ORANGE, (xpos, ypos), (xpos+tank_length*10*math.cos(nose_direction), ypos+tank_length*10*math.sin(nose_direction)))
        
           
def scale_converter(real_distance, SCALE_FACTOR):
    scaled_distance = real_distance / SCALE_FACTOR
    return round(scaled_distance)



#initialize relative center of the simulation and any planets/moons below it
#Planetary Mass: name, mass, radius, parent_planet, orbital_radius
#sun = Mass("Sun", 1988500*(10**24), 20000, None, 0)
#mercury = Mass("Mercury", 0.330*(10**24), 2439, sun, 57.9*(10**5))
#venus = Mass("Venus", 4.87*(10**24), 6052, sun, 108.2*(10**5))
earth = Mass("Earth", 5.97*(10**24), 6378, None, 0)
moon = Mass("Moon", 0.073*(10**24), 1737, earth, 0.384*(10**6))


planet_list = [earth, moon]

#SpaceCraft: name, mass, length, initial_parent, orbital_radius, true_anomaly=0, orbital_velo=None
#if no value inputed for initial velocity, it will assume a circular orbit
craft = SpaceCraft("craft", 10, 3, earth, 7878, 0)



#parameters: display dimensions, simulation width included in the display window, iteration pause interval
def main(screen_width, screen_height, simulation_width, time_step, iteration_pause, lead_length):

    #display infomation and dimensions
    DP_WIDTH, DP_HEIGHT = screen_width, screen_height    
    REAL_WIDTH = simulation_width #raw width of the display in km
    SCALE_FACTOR = round(REAL_WIDTH / DP_WIDTH) #ratio of km:1 pixel
    TARGET = craft #either a class object or x-y coordinates
    step_options = (0.001, 0.01, 0.1, 0.2, 1, 5) #base, 5 min orbit, 30 second, time-skip

    #craft will always have the 0 index
    target_list = [craft]
    for planet in planet_list:
        target_list.append(planet)
    
    #sets up the initial conditions of the universe relative to some point/planet
    relative_center = None
    for planet in planet_list:
        if planet.parent_planet == None:
            relative_center = planet
            planet.xpos, planet.ypos = 0, 0
            planet.xvelo, planet.xvelo = 0, 0
            break

    #places each planet in orbits around their parent planets, everything of which is relative to the center
    for planet in planet_list:
        if planet != relative_center:
            planet.xpos = planet.orbital_radius*math.cos(planet.true_anomaly) + planet.parent_planet.xpos
            planet.ypos = planet.orbital_radius*math.sin(planet.true_anomaly) + planet.parent_planet.ypos
            planet.xvelo += planet.parent_planet.xvelo
            planet.yvelo += planet.parent_planet.yvelo
            
    
    
    craft.xpos = craft.orbital_radius*math.cos(craft.true_anomaly) + craft.initial_parent.xpos
    craft.ypos = craft.orbital_radius*math.sin(craft.true_anomaly) + craft.initial_parent.ypos
    craft.xvelo += craft.initial_parent.xvelo
    craft.yvelo += craft.initial_parent.yvelo

    #initializing planet and craft positions
    #zero index is the current real position of the simulation
    for planet in planet_list:
        planet.lead_positions = [(planet.xpos, planet.ypos)]
        planet.lead_velocities = [(planet.xvelo, planet.yvelo)]
    
    craft_positions = [(craft.xpos, craft.ypos)]
    craft_velocities = [(craft.xvelo, craft.yvelo)]

    thrust_change = True

    #initializes pygame library tools and display window
    pygame.init()
    screen = pygame.display.set_mode((DP_WIDTH, DP_HEIGHT))
    
    counter = 0 #iteration counter to help understand the speed of each iteration
    #event loop
    running = True
    while running:
        counter += 1

        #pygame.event list keeps track of all keypress/mouseclicks that occur
        for event in pygame.event.get():
            
            #closes window when exit button pressed
            if event.type == pygame.QUIT:
                running = False

            if event.type == pygame.KEYDOWN:
                
                #zoom in or out using up and down keys, respectively
                if event.key == pygame.K_1:
                    SCALE_FACTOR = round(0.5 * SCALE_FACTOR, 5)
                if event.key == pygame.K_2:
                    SCALE_FACTOR = round(1.5 * SCALE_FACTOR, 5)
                #switch the target object
                if event.key == pygame.K_q:
                    if target_list.index(TARGET)+1 == len(target_list):
                        TARGET = target_list[0]
                    else:
                        TARGET = target_list[target_list.index(TARGET)+1]
                if event.key == pygame.K_e:
                    if target_list.index(TARGET)+1 == len(target_list):
                        TARGET = target_list[0]
                    else:
                        TARGET = target_list[target_list.index(TARGET)-1]   
                #change the simulation speed
                if event.key == pygame.K_z:
                    time_step = time_step * 10
                    #if step_options.index(time_step)+1 != len(step_options):
                    #    time_step = step_options[step_options.index(time_step)+1]
                if event.key == pygame.K_x:
                    time_step = time_step / 10
                    #if step_options.index(time_step) > 0:
                    #    time_step = step_options[step_options.index(time_step)-1]

                # if event.key == pygame.K_w or event.key == pygame.K_s:
                #     thrust_change = True
                #     #forward/prograde: 1    backwards/retrograde: -1
                #     if event.key == pygame.K_w:
                #         direction = 1
                #     else:
                #         direction = -1
                #     if craft.fuel_remaining > 0:
                #         craft_velo = math.sqrt(craft.xvelo**2 + craft.yvelo**2)
                #         craft_direction = math.atan2(craft.yvelo, craft.xvelo)

                #         thrust_x = (direction*0.01*craft_velo)*math.cos(craft_direction)
                #         thrust_y = (direction*0.01*craft_velo)*math.sin(craft_direction)

                #         craft.fuel_remaining -= 1
                #     else:
                #         print("no fuel remaining!!!")
                # else:
                #     thrust_x, thrust_y = 0, 0

                
        #print(moon.name, ": ", planet_positions[moon], "---", counter)                 
        #creates a list of future planet positions
        if thrust_change == True:
            start = time.time()
            for i in range(lead_length-1):

                for planet in planet_list:

                    #vectors reffered in [magnitude, angle] pairs
                    #all angles drawn from the planet
                    gravity_vectors = []

                    for other in planet_list:
                        if other == planet:
                            continue
                        deltax = planet.lead_positions[i][0] - other.lead_positions[i][0]
                        deltay = planet.lead_positions[i][1] - other.lead_positions[i][1]
                        distance = math.sqrt((deltax**2) + (deltay**2))
                        #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
                        force_gravity = ((G*planet.mass*other.mass) / (1000*distance)**2) / 1000
                        theta = math.atan2(deltay, deltax)

                        gravity_vectors.append((force_gravity, theta))

                    net_xforce, net_yforce = 0, 0
                    for vector in gravity_vectors:
                        
                        net_xforce += vector[0] * -math.cos(vector[1])
                        net_yforce += vector[0] * -math.sin(vector[1])

                    net_xaccel = net_xforce / planet.mass
                    net_yaccel = net_yforce / planet.mass

                    #calculates the change in velocity over a given time step interval
                    xvelo = planet.lead_velocities[i][0] + (net_xaccel * time_step) 
                    yvelo = planet.lead_velocities[i][1] + (net_yaccel * time_step) 

                    planet.lead_velocities.append((xvelo, yvelo))

                    #determines the change in position over the same time step interval
                    xpos = planet.lead_positions[i][0] + (xvelo * time_step)
                    ypos = planet.lead_positions[i][1] + (yvelo * time_step)

                    planet.lead_positions.append((xpos, ypos))

                craft_influences = []
                for planet in planet_list:
                
                    deltax = craft_positions[i][0] - planet.lead_positions[i][0]
                    deltay = craft_positions[i][1] - planet.lead_positions[i][1]
                    distance = math.sqrt((deltax**2) + (deltay**2))
                    #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
                    force_gravity = ((G*craft.mass*planet.mass) / (1000*distance)**2) / 1000
                    theta = math.atan2(deltay, deltax)
                    craft_influences.append((force_gravity, theta))

                net_xforce, net_yforce = 0, 0
                for vector in craft_influences:
                    
                    net_xforce += vector[0] * -math.cos(vector[1])
                    net_yforce += vector[0] * -math.sin(vector[1])

                net_xaccel = net_xforce / craft.mass
                net_yaccel = net_yforce / craft.mass

                ##calculates the change in velocity over a given time step interval
                xvelo = craft_velocities[i][0] + (net_xaccel * time_step)
                yvelo = craft_velocities[i][1] + (net_yaccel * time_step)	

                craft_velocities.append((xvelo, yvelo))

                #determines the change in position over the same time step interval
                xpos = craft_positions[i][0] + xvelo * time_step
                ypos = craft_positions[i][1] + yvelo * time_step

                craft_positions.append((xpos, ypos))

        else:
            start = time.time()
            for planet in planet_list:

                #vectors reffered in [magnitude, angle] pairs
                #all angles drawn from the planet
                gravity_vectors = []

                for other in planet_list:
                    if other == planet:
                        continue
                    deltax = planet.lead_positions[-1][0] - other.lead_positions[-1][0]
                    deltay = planet.lead_positions[-1][1] - other.lead_positions[-1][0]
                    distance = math.sqrt((deltax**2) + (deltay**2))
                    #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
                    force_gravity = ((G*planet.mass*other.mass) / (1000*distance)**2) / 1000
                    theta = math.atan2(deltay, deltax)

                    gravity_vectors.append((force_gravity, theta))

                net_xforce, net_yforce = 0, 0
                for vector in gravity_vectors:
                    
                    net_xforce += vector[0] * -math.cos(vector[1])
                    net_yforce += vector[0] * -math.sin(vector[1])

                net_xaccel = net_xforce / planet.mass
                net_yaccel = net_yforce / planet.mass

                #calculates the change in velocity over a given time step interval
                xvelo = planet.lead_velocities[-1][0] + (net_xaccel * time_step)
                yvelo = planet.lead_velocities[-1][1] + (net_yaccel * time_step)	

                planet.lead_velocities.append((xvelo, yvelo))

                #determines the change in position over the same time step interval
                xpos = planet.lead_positions[-1][0] + (xvelo * time_step)
                ypos = planet.lead_positions[-1][1] + (yvelo * time_step)

                planet.lead_positions.append((xpos, ypos))
            
            craft_influences = []
            for planet in planet_list:
            
                deltax = craft_positions[-1][0] - planet.lead_positions[-1][0]
                deltay = craft_positions[-1][1] - planet.lead_positions[-1][1]
                distance = math.sqrt((deltax**2) + (deltay**2))
                #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
                force_gravity = ((G*craft.mass*planet.mass) / (1000*distance)**2) / 1000
                theta = math.atan2(deltay, deltax)
                craft_influences.append((force_gravity, theta))

            net_xforce, net_yforce = 0, 0
            for vector in craft_influences:
                
                net_xforce += vector[0] * -math.cos(vector[1])
                net_yforce += vector[0] * -math.sin(vector[1])

            net_xaccel = net_xforce / craft.mass
            net_yaccel = net_yforce / craft.mass

            #calculates the change in velocity over a given time step interval
            xvelo = craft_velocities[-1][0] + net_xaccel * time_step
            yvelo = craft_velocities[-1][1] + net_yaccel * time_step	

            craft_velocities.append((xvelo, yvelo))
            #determines the change in position over the same time step interval
            xpos = craft_positions[-1][0] + xvelo * time_step
            ypos = craft_positions[-1][1] + yvelo * time_step
            
            craft_positions.append((xpos, ypos))
            

        
        for planet in planet_list:
            planet.xpos, planet.ypos = planet.lead_positions[0][0], planet.lead_positions[0][1]
            planet.xvelo, planet.yvelo = planet.lead_velocities[0][0], planet.lead_velocities[0][1]
        craft.xpos, craft.ypos = craft_positions[0][0], craft_positions[0][1]
        craft.xvelo, craft.yvelo = craft_velocities[0][0], craft_velocities[0][1]


        DP_CENTER_X = DP_WIDTH/2 + (-1 * scale_converter(TARGET.xpos, SCALE_FACTOR))
        DP_CENTER_Y = DP_HEIGHT/2 + (-1 * scale_converter(TARGET.ypos, SCALE_FACTOR))

        screen.fill(BLACK)
        #draw planets
        for planet in planet_list:
            dp_position = (DP_CENTER_X + scale_converter(planet.lead_positions[0][0], SCALE_FACTOR), DP_CENTER_Y + scale_converter(planet.lead_positions[0][1], SCALE_FACTOR))
            pygame.draw.circle(screen, WHITE, dp_position, scale_converter(planet.radius, SCALE_FACTOR))
            pygame.draw.polygon(screen, ORANGE, [dp_position, [dp_position[0]+5, dp_position[1]-10], [dp_position[0]-5, dp_position[1]-10]])
        
        #draw craft
        craft_dp_pos = (DP_CENTER_X + scale_converter(craft_positions[0][0], SCALE_FACTOR), DP_CENTER_Y + scale_converter(craft_positions[0][1], SCALE_FACTOR))
        craft.drawCraft(screen, craft_dp_pos, SCALE_FACTOR)
        #draw the craft's future path
        for i in range(lead_length-1):
            dp_pos = (DP_CENTER_X + scale_converter(craft_positions[i][0], SCALE_FACTOR), DP_CENTER_Y + scale_converter(craft_positions[i][1], SCALE_FACTOR))
            pygame.draw.circle(screen, BLUE, dp_pos, 1)
        #craft triangle marker
        pygame.draw.polygon(screen, GREEN, (craft_dp_pos, (craft_dp_pos[0]+5, craft_dp_pos[1]-10), (craft_dp_pos[0]-5, craft_dp_pos[1]-10)))

        for planet in planet_list:
            planet.lead_positions.pop(0)
            planet.lead_velocities.pop(0)
        craft_positions.pop(0)
        craft_velocities.pop(0)
        
        thrust_change = False
        
        pygame.display.set_caption(str(counter))
        pygame.display.update()
        finish = time.time()
        time.sleep(iteration_pause)

#parameters: real width included in the display window, iteration pause interval
main(1000, 1000, 1000000, 0.01, 0.001, 500)